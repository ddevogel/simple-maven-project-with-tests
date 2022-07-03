import groovy.json.JsonSlurper

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
    def String[] hosts
    def Db database
    def Queue queue
    def NukeSvc nuke
    def Chargebee chargebee
    def Object secrets

    Config(Object json, secrets) {
        this.secrets = secrets
        this.set(json)
    }
    @NonCPS
    def Config extend(json) {
        this.set(json)
        return this        
    }
    @NonCPS
    def String getFileContent() {
        return String.format("%s\n%s\n%s\n%s",
            this.database.toString(),
            this.queue.toString(),
            this.nuke.toString(),
            this.chargebee.toString()              
        )        
        .replace("**db_pwd**", this.secrets.db_pwd)
        .replace("**rmq_pwd**", this.secrets.rmq_pwd) 
        .replace("**nuke_svc_key**", this.secrets.nuke_svc_key) 
        .replace("**chargebee_key**", this.secrets.chargebee_key) 
    }
    @NonCPS    
    def private set(json) {
        if (json.hosts != null) {
            this.hosts = json.hosts
        } 
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


}

@NonCPS
def parseJsonText(String json) {
    def slurper = new JsonSlurper()
    def result = new HashMap<>(slurper.parseText(json))    
    return result
}  

@NonCPS
def build(environment, text, secrets) {
    def jsonText = parseJsonText(text)
    def jsonSecrets = parseJsonText(secrets)
    def config = new Config(jsonText["common"], jsonSecrets)
        .extend(jsonText[environment.toLowerCase()])

    return config
}

return this

/*
To debug
    - comment out //@NonCPS everywhere
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
// config = build("Dev", text, secrets)
// println(config.hosts)
// println(config.getFileContent())
