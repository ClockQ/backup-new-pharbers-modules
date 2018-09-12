package com.pharbers.pattern2

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import com.pharbers.pattern2.common.{excute, timeout}
import com.pharbers.pattern2.detail.commonresult
import com.pharbers.pattern2.error.commonerror
import com.pharbers.pattern2.steps.commonstep
import pattern.manager.SequenceSteps
import play.api.libs.json.Json.toJson

class Gateway extends Actor with ActorLogging {

    var originSender : ActorRef = null
    var next : ActorRef = null

    def receive = {
        case excute(sequence) => {
            originSender = sender
            sequence.steps match {
                case Nil => originSender ! new commonerror(0, "error")
                case head :: tail => {
                    head match {
//                        case p : ParallelMessage => {
//                            next = context.actorOf(ScatterGatherActor.prop(self, MessageRoutes(tail, msr.rst)), "gate")
//                            next ! head
//                        }
                        case c : commonstep => {
                            next = context.actorOf(PipeFilter.prop(self, SequenceSteps(tail, sequence.cr)), "gate")
                            next ! head
                        }
                    }

                    context.watch(next)
                }
            }
        }
        case rst : commonresult => {
            originSender ! rst
            cancelActor
        }
        case err : commonerror => {
            originSender ! err
            cancelActor
        }
        case timeout() => {
            originSender ! toJson("timeout")
            cancelActor
        }
        case Terminated(actorRef) => println("Actor {} terminated", actorRef)
        case _ => Unit
    }

    def cancelActor = {
        context.stop(self)
    }
}
