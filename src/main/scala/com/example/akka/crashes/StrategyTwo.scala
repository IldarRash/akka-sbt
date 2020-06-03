package com.example.akka.crashes

import akka.actor.SupervisorStrategy.{Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, AllForOneStrategy, OneForOneStrategy, Props, Terminated}

package dbstrategy3 {

import akka.actor.PoisonPill
import akka.actor.SupervisorStrategy.Restart
import com.example.akka.crashes.aia.faulttolerance.dbstrategy2.FileWatcher.{NewFile, SourceAbandoned}
import com.example.akka.crashes.aia.faulttolerance.dbstrategy2.LogProcessor.LogFile
import com.example.akka.crashes.aia.faulttolerance.dbstrategy2.{CorruptedFileException, DbBrokenConnectionException, DbWriter, DiskError, FileWatchingAbilities, LogProcessor}

object LogProcessingApp extends App {
  val sources = Vector("file:///source1/", "file:///source2/")
  val system = ActorSystem("logprocessing")
  // create the props and dependencies
  val databaseUrl = "http://mydatabase"

  val writerProps = Props(new DbWriter(databaseUrl))
  val dbSuperProps = Props(new DbSupervisor(writerProps))
  val logProcSuperProps = Props(
    new LogProcSupervisor(dbSuperProps))
  val topLevelProps = Props(new FileWatcherSupervisor(
    sources,
    logProcSuperProps))
  system.actorOf(topLevelProps)
}



class FileWatcherSupervisor(sources: Vector[String],
                            logProcSuperProps: Props)
  extends Actor {

  var fileWatchers: Vector[ActorRef] = sources.map { source =>
    val logProcSupervisor = context.actorOf(logProcSuperProps)
    val fileWatcher = context.actorOf(Props(
      new FileWatcher(source, logProcSupervisor)))
    context.watch(fileWatcher)
    fileWatcher
  }

  override def supervisorStrategy = AllForOneStrategy() {
    case _: DiskError => Stop
  }

  def receive = {
    case Terminated(fileWatcher) =>
      fileWatchers = fileWatchers.filterNot(w => w == fileWatcher)
      if (fileWatchers.isEmpty) self ! PoisonPill
  }
}



class FileWatcher(sourceUri: String,
                  logProcSupervisor: ActorRef)
  extends Actor with FileWatchingAbilities {
  register(sourceUri)

  def receive = {
    case NewFile(file, _) =>
      logProcSupervisor ! LogFile(file)
    case SourceAbandoned(uri) if uri == sourceUri =>
      self ! PoisonPill
  }
}

class DbSupervisor(writerProps: Props) extends Actor {
  override def supervisorStrategy = OneForOneStrategy() {
    case _: DbBrokenConnectionException => Restart
  }
  val writer = context.actorOf(writerProps)
  def receive = {
    case m => writer forward (m)
  }
}


class LogProcSupervisor(dbSupervisorProps: Props)
  extends Actor {
  override def supervisorStrategy = OneForOneStrategy() {
    case _: CorruptedFileException => Resume
  }
  val dbSupervisor = context.actorOf(dbSupervisorProps)
  val logProcProps = Props(new LogProcessor(dbSupervisor))
  val logProcessor = context.actorOf(logProcProps)

  def receive = {
    case m => logProcessor forward (m)
  }
}
