import groovy.json.JsonSlurperClassic

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
        try {
            if(json == null) return
            this.host = json.host ?: this.host
            this.name = json.name ?: this.name
            this.user = json.user ?: this.user
            this.password = json.password ?: this.password
            this.sslmode  = json.sslmode ?: this.sslmode
        } catch(Exception e) {
            println(ex.getMessage())
        }
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
class Queue {
    String host
    Integer port
    String exch
    String queue
    String user
    String password   

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
        this.no_ack = json.no_ack ?: this.no_ack
    }

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
    def String toString() {
        return String.format("NUKE_SVC_URL=%s\nNUKE_SVC_KEY=%s",
         this.url,
         this.secret
        )     
}
class Chargebee {
    String site
    String key

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
        this.key = json.key ?: this.key
    }
    def String toString() {
        return String.format('CHARGEBEE_SITE="%s"\nCHARGEBEE_FULL_ACCESS_KEY="%s"',
         this.site,
         this.key
        )     

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
        return String.format(%s\n%s\n%s\n%s\n,
            this.database.toString(),
            this.queue.toString(),
            this.nuke.toString(),
            this.chargebee.toString(),              
          )
    
@NonCPS
def getFileContent(environment, text) {
    json = new JsonSlurperClassic().parseText(text)
    //println(json[environment.toLowerCase()])
    def config = new Config(json["common"]).extend(json[environment.toLowerCase()])
    //config = new Config(json["common"]).Extend(json[environment.toLowerCase()])
    //File file = new File("./out.txt")
    return config.toString()
}

return this

// println(getConfig("dev")["database"]["name"])

// def ys = new JsonSlurper()
// def config = ys.parseText(('config.json' as File).text)
 // println(config).dev.database)
// Config c = new Config(config["common"]).Extend(config["dev"])

// println(c.database.name)
// println(c.queue.port)
// println(c.chargebee.site)
