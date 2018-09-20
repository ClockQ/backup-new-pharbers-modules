import akka.actor.{ActorRef, ActorSystem}
import com.pharbers.xlsxToCsvHeader
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await

case class startTest() {
    def doTest(): Unit ={
        val args = Map("ym" -> "1804")
        implicit val t: Timeout = 1200 minutes
        val system = ActorSystem("unitTest")
        val testHeader: ActorRef = system.actorOf(xlsxToCsvHeader.props())
        val r = testHeader ? xlsxToCsvHeader.testJob(args)
        Await.result(r.mapTo[String], t.duration)
    }
}
