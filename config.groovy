import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap

class Db {
    String host
    String name
    String user
    String password
    String sslmode

    Db(json) {
        this.set(json)
    }
    @NonCPS
    def extend(json) {
        this.set(json)
    }
    @NonCPS
    def private set(json) {
        if(json == null) return
        this.host = json.host ?: this.host
        this.name = json.name ?: this.name
        this.user = json.user ?: this.user
        this.password = json.password ?: this.password
        this.sslmode  = json.sslmode ?: this.sslmode
    }
    @NonCPS
    def String toString() {
        return String.format("DATABASE_CONNECTION='host=%s dbname=%s user=%s password=%s sslmode=%s'", 
         this.host,
         this.name,
         this.user,
         this.password,
         this.sslmode
        )  
    }  
}
class Queue {
    String host
    Integer port
    String exch
    String queue
    String user
    String password 
    Integer no_ack  

    Queue(json) {
        this.set(json)
    }
    @NonCPS
    def extend(json) {
        this.set(json)
    }
    @NonCPS
    def private set(json) {
        if(json == null) return
        this.host = json.host ?: this.host
        this.port = json.port ?: this.port
        this.exch = json.exch ?: this.exch
        this.queue = json.queue ?: this.queue
        this.user  = json.user ?: this.user 
        this.password = json.password ?: this.password
        this.no_ack = (json.no_ack != null) ? json.no_ack : this.no_ack
    }
    @NonCPS
    def String toString() {
        return String.format("QUEUE_HOST=%s\nQUEUE_PORT=%d\nQUEUE_EXCH=%s\nQUEUE_QUEUE=%s\nQUEUE_USER=%s\nQUEUE_PWD=%s\nQUEUE_NO_ACK=%d",
         this.host,
         this.port,
         this.exch,
         this.queue,
         this.user,
         this.password,
         this.no_ack
        ) 
    }
}
class NukeSvc {
    String url
    String secret

    NukeSvc(json) {
        this.set(json)
    }
    @NonCPS
    def extend(json) {
        this.set(json)
    }
    @NonCPS
    def private set(json) {
        if(json == null) return        
        this.url = json.url ?: this.url
        this.secret = json.secret ?: this.secret
    }
    @NonCPS
    def String toString() {
        return String.format("NUKE_SVC_URL=%s\nNUKE_SVC_KEY=%s",
         this.url,
         this.secret
        )    
    } 
}
class Chargebee {
    String site
    String api_key

    Chargebee(json) {
        this.set(json)
    }
    @NonCPS
    def extend(json) {
        this.set(json)
    }
    @NonCPS
    def private set(json) {
        if(json == null) return        
        this.site = json.site ?: this.site
        this.api_key = json.api_key ?: this.api_key
    }
    @NonCPS
    def String toString() {
        return String.format('CHARGEBEE_SITE=%s\nCHARGEBEE_FULL_ACCESS_KEY=%s',
         this.site,
         this.api_key
        )     
    }
}
class Config {
    Db database
    Queue queue
    NukeSvc nuke
    Chargebee chargebee

    Config(Object json) {
        //if (json == null) return null
        if (this.database == null) {
            this.database = new Db(json.database)
        } else {
            this.database.extend(json.database)
        }
        if (this.queue == null) {
            this.queue = new Queue(json.queue)
        } else {
            this.queue.extend(json.queue)
        }
        if (this.nuke == null) {
            this.nuke = new NukeSvc(json.nuke_svc)
        } else {
            this.nuke.extend(json.nuke_svc)
        }        
        if (this.chargebee == null) {
            this.chargebee = new Chargebee(json.chargebee)
        } else {
            this.chargebee.extend(json.chargebee)     
        }
    }
    @NonCPS
    def Config extend(json) {
        if (this.database == null) {
            this.database = new Db(json.database)
        } else {
            this.database.extend(json.database)
        }
        if (this.queue == null) {
            this.queue = new Queue(json.queue)
        } else {
            this.queue.extend(json.queue)
        }
        if (this.nuke == null) {
            this.nuke = new NukeSvc(json.nuke_svc)
        } else {
            this.nuke.extend(json.nuke_svc)
        }        
        if (this.chargebee == null) {
            this.chargebee = new Chargebee(json.chargebee)
        } else {
            this.chargebee.extend(json.chargebee)
        }
        return this        
    }
    @NonCPS
    def String toString() {
        return String.format("%s\n%s\n%s\n%s",
            this.database.toString(),
            this.queue.toString(),
            this.nuke.toString(),
            this.chargebee.toString()              
          )
    }
}

@NonCPS
static def convertLazyMapToLinkedHashMap(def value) {
    if (value instanceof LazyMap) {
      Map copy = [:]
      for (pair in (value as LazyMap)) {
        copy[pair.key] = convertLazyMapToLinkedHashMap(pair.value)
      }
      println("here")
      return copy
    } else {
      return value
    }
}

@NonCPS
def parseJsonText(String json) {
    slurper = new JsonSlurper()
    parsed = slurper.parseText(json)
    result = convertLazyMapToLinkedHashMap(parsed) 
    return result
}  

@NonCPS
def String build(environment, text, secrets) {
    //slurper = new JsonSlurper()
    json = parseJsonText(text)
    _secrets = parseJsonText(secrets)
    println(_secrets)
    def config = new Config(json["common"]).extend(json[environment.toLowerCase()]).toString()
        .replace("**db_pwd**", _secrets.db_pwd)
        .replace("**rmq_pwd**", _secrets.rmq_pwd) 
        .replace("**nuke_svc_key**", _secrets.nuke_svc_key) 
        .replace("**chargebee_key**", _secrets.chargebee_key) 

    return config.toString()
}

return this

/*
To debug
    - comment out @NonCPS everywhere
    - comment out last "return this" above
    - uncomment debug code below
*/
// def text  = new File("config.json").text
// def secrets = """{
// "db_pwd":"DBPWD",
// "rmq_pwd":"RMQPWD",
// "nuke_svc_key":"NUKEKEY",
// "chargebee_key":"CBKEY"
// }"""
// println(build("Dev", text, secrets))
