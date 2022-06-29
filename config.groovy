import groovy.json.JsonSlurperClassic

class Db{
    String host
    String name
    String user
    String password
    String sslmode
    Db(json){
        this.set(json)
    }
    def Extend(json){
        this.set(json)
    }
    def private set(json){
        if(json == null) return
        this.host = json.host ?: this.host
        this.name = json.name ?: this.name
        this.user = json.user ?: this.user
        this.password = json.password ?: this.password
        this.sslmode  = json.sslmode ?: this.sslmode        
    }
}
class Queue {
    String host
    Integer port
    String exch
    String queue
    String user
    String password   
    Queue(json){
        this.set(json)
    }
    def Extend(json){
        this.set(json)
    }
    def private set(json){
        if(json == null) return
        this.host = json.host ?: this.host
        this.port = json.port ?: this.port
        this.exch = json.exch ?: this.exch
        this.queue = json.queue ?: this.queue
        this.user  = json.user ?: this.user 
        this.password = json.password ?: this.password
    }
}
class NukeSvc {
    String url
    String secret
    NukeSvc(json) {
        this.set(json)
    }
    def Extend(json){
        this.set(json)
    }
    def private set(json){
        if(json == null) return        
        this.url = json.url ?: this.url
        this.secret = json.secret ?: this.secret
    }
}
class Chargebee{
    String site
    String key
    Chargebee(json){
        this.set(json)
    }
    def Extend(json){
        this.set(json)
    }
    def private set(json){
        if(json == null) return        
        this.site = json.site ?: this.site
        this.key = json.key ?: this.key
    }
}
class Config {
    Db database
    Queue queue
    NukeSvc nuke
    Chargebee chargebee

    Config(Object json) {
        //if (json == null) return null
        if (this.database == null){
            this.database = new Db(json.database)
        } else {
            this.database.Extend(json.database)
        }
        if (this.queue == null){
            this.queue = new Queue(json.queue)
        } else {
            this.queue.Extend(json.queue)
        }
        if (this.nuke == null){
            this.nuke = new NukeSvc(json.nuke_svc)
        } else {
            this.nuke.Extend(json.nuke_svc)
        }        
        if (this.chargebee == null){
            this.chargebee = new Chargebee(json.chargebee)
        } else {
            this.chargebee.Extend(json.chargebee)     
        }
    }
    def Config Extend(json){
        if (this.database == null){
            this.database = new Db(json.database)
        } else {
            this.database.Extend(json.database)
        }
        if (this.queue == null){
            this.queue = new Queue(json.queue)
        } else {
            this.queue.Extend(json.queue)
        }
        if (this.nuke == null){
            this.nuke = new NukeSvc(json.nuke_svc)
        } else {
            this.nuke.Extend(json.nuke_svc)
        }        
        if (this.chargebee == null){
            this.chargebee = new Chargebee(json.chargebee)
        } else {
            this.chargebee.Extend(json.chargebee)
        }
        return this        
    }
}

def getFileContent(environment, text) {
    json = new JsonSlurperClassic().parseText(text)
    config = new Config(json["common"]).Extend(json[environment.toLowerCase()])
    //File file = new File("./out.txt")
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
