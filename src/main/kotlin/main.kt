import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.eclipse.jetty.util.log.Slf4jLog
import spark.Request
import spark.Response
import spark.Spark.*;
import java.sql.DriverManager


fun main(args: Array<String>){
    port(5000)
    staticFiles.location("/public")

    get("/all"){req, res -> getUsers(req, res)}
    post("/add"){req, res -> addUser(req, res)}
    post("/mass"){req, res -> populate(req, res)}
    delete("/purge"){req, res -> purge(req, res)}
    delete("/delete"){req, res -> delete(req, res)}
    put("/update"){req, res -> update(req, res)}

}

var dbPath = "jdbc:h2:./users";

fun addUser(req: Request, res:Response): Int{
    val user = Gson().fromJson(req.body(), User::class.java)
    val conn = DriverManager.getConnection(dbPath, "sa", "")
    val statement = conn.createStatement()
    val rs = statement.execute(
        "INSERT INTO USERS (name, surname, age, pesel) values('" +
            user.name + "','" + user.surname + "','" +
            user.age + "','" + user.pesel + "');"
    )
    conn.close()
    return 200
}

fun getUsers(req: Request, res: Response): String {
    res.type("application/json")
    val users = mutableListOf<User>()
    val conn = DriverManager.getConnection(dbPath, "sa", "")
    val statement = conn.createStatement()
    val rs = statement.executeQuery("SELECT * FROM USERS")
    while (rs.next()){
        users.add(User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("surname"),
            rs.getInt("age"),
            rs.getString("pesel")
        ))
    }
    conn.close()
    return Gson().toJson(users)
}

fun populate(req: Request, res: Response){
    val users = Gson().fromJson(req.body(), Array<User>::class.java)
    val conn = DriverManager.getConnection(dbPath, "sa", "")
    val statement = conn.createStatement()
    users.forEach { user -> statement.execute(
        "INSERT INTO USERS (name, surname, age, pesel) values('" +
                user.name + "','" + user.surname + "','" +
                user.age + "','" + user.pesel + "');"
    ) }
    conn.close()
}

fun purge(req: Request, res: Response){
    val conn = DriverManager.getConnection(dbPath, "sa", "")
    val statement = conn.createStatement()
    val rs = statement.execute("DELETE FROM USERS")
    conn.close()
}

fun delete(req: Request, res: Response){
    println(req.body())
    val user = Gson().fromJson(req.body(), User::class.java)
    val conn = DriverManager.getConnection(dbPath, "sa", "")
    val statement = conn.createStatement()
    val rs = statement.execute("DELETE FROM USERS WHERE id=" + user.id)
    conn.close()
}

fun update(req: Request, res: Response){
    println(req.body())
    val user = Gson().fromJson(req.body(), User::class.java)
    val conn = DriverManager.getConnection(dbPath, "sa", "")
    val statement = conn.createStatement()
    val rs = statement.execute(
        "UPDATE USERS " +
            "SET name='" + user.name + "', surname='" + user.surname + "', pesel=" + user.pesel + ", age=" + user.age+
            "WHERE id="+ user.id)
    conn.close()
}