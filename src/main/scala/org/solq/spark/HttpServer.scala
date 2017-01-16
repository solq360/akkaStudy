package org.solq.spark

import scala.io.StdIn



import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import org.solq.spark.model.QuerySpark
import scala.tools.asm.Item
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
 
class HttpServer {
   var objManager = new ObjectMapper;
   objManager.registerModule(DefaultScalaModule)
  def start() {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val um: Unmarshaller[HttpEntity, QuerySpark] = {
      Unmarshaller.byteStringUnmarshaller.mapWithCharset { (data, charset) =>
        println(new String(data.toArray))
        objManager.readValue(data.toArray, classOf[QuerySpark])
      }
    }
    var route: Route = (get & (path("index") | path(""))) {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, indexHtml))
    } ~
//      (get & path("test")) {
//        decodeRequest {
//          entity(as[String]) { jsonStr =>
//            complete(jsonStr)
//          }
//        }
//      } ~
      (pathPrefix("query") & get) {
        complete(s"<h1>{id}</h1>")
      } ~
      (post & path("query")) {

        entity(as[QuerySpark]) {
          querySpark => complete(querySpark.toString())
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 9988)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    while (true) {
      val command = StdIn.readLine()
      command match {
        case "stop" => {
          bindingFuture.flatMap(_.unbind()) // trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
        }
      }
    }
  }

  val indexHtml = """
<script type="text/javascript">  
    
(function(){
var debugFlag = false;
var url = "https://code.jquery.com/jquery-1.12.3.min.js";
window.embed_script = embed_script;
//执行主程序
embed_script(url,Main);

function Main(){
  $('#submit').click(function(){
  
    var d ={
      dt: $('#id').val(),
      sql : $('#name').val()
    }
    d = JSON.stringify(d);
    console.log(d)
    $.ajax({
    		dataType: 'json',
    		type : "POST",
    		url : "query",
    		contentType : "application/json",
    		data : d,
    		success : function(json) {
    			console.log(json)
    		}
    	});		
    return false;
  });
}
//////////////////////////////////////////////////////////////////////////////////////////
/**
嵌入script
*/
function embed_script(url ,cb){
 	var script = document.createElement('script');
    script.setAttribute('type', 'text/javascript');
    script.setAttribute('src', url);    
	document.getElementsByTagName('head')[0].appendChild(script);
	script.onload= cb;
}
 
})();
</script>
<form action="query" method="post" enctype='application/json'>
  <p>SourceType: <input type="text" id="dt" /></p>
  <p>sql: <input type="text" id="sql" /></p>
  <input id="submit" value="Submit" type="submit"  />
</form>
    """
}