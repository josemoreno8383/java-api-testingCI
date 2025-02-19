import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import utils.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApiRestAssuredTests {

    private String selectedUserId;
    private String selectedUserName;
    private String selectedUserEmail;

    @BeforeClass
    public void setup(){
        baseURI = "https://gorest.co.in/public/v2";
    }


    /**
     * Este test tiene como objetivo: obtener la lista de usuario
     */

    @Test
    public void deberiaListarUsuariosExitosamente(){
        given()
                .accept(ContentType.JSON)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",everyItem(notNullValue()))
                .body("email",everyItem(matchesPattern("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")));

    }


    /**
     *  Este test tiene como objetivo: crear un usuario nuevo cada vez que se ejecute
     *  Nota: hace uso de createRequestBody y generateRandomEmail
     */

    private Map<String, String> createRequestBody(String name,String email, String gender, String status){
        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("gender",gender);
        requestBody.put("email", email);
        requestBody.put("status", status);

        return requestBody;
    }

    public String generateRandomEmail(){
        String uniqueId = UUID.randomUUID().toString().substring(0,8);
        return "usuarioDePruebas" + uniqueId + "@test.com";
    }

    @Test
    public void deberiaCrearUnNuevoUsuarioExitosamente(){
        String randomEmail = generateRandomEmail();
        String name ="usuario de pruebas";

        Response response =given()
                .header("Accept","application/json")
                .contentType(ContentType.JSON)
                .header("Authorization", Config.getToken())
                .body(createRequestBody(name,randomEmail,"male","active"))
        .when()
                .post("/users")
        .then()
               // .log().all()
                .statusCode(201)
                .extract().response();
        System.out.println("Se creó el usuario de id: "+response.jsonPath().getString("id"));


    }

    /**
     * Este test tiene como objetivo: consultar un usuario existente e imprime datos por consola
     * Nota: este test se apoya de un metodo previo (BeforeMethod) que detecta un usuario existente
     */

    @BeforeMethod(onlyForGroups = "test-GetUserDetailById")
    public void getExistingUserId(){

        Response response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .extract().response();

        selectedUserId = response.jsonPath().getString("[0].id");
        selectedUserName = response.jsonPath().getString("[0].name");
        selectedUserEmail = response.jsonPath().getString("[0].email");
        System.out.println("El id del usuario existente seleccionado es: "+selectedUserId);

    }

    @Test(groups = "test-GetUserDetailById")
    public void deberiaObtenerDetalleDeUnUsuarioPorIdExitosamente(){
        System.out.println("El usuario que se consultara es el de id: " + selectedUserId);

        Response response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/users/"+selectedUserId)
        .then()
                .statusCode(200)
                .body("id",equalTo(Integer.parseInt(selectedUserId)))
                .body("email",equalTo(selectedUserEmail))
                .body("name",equalTo(selectedUserName))
                .extract().response();
        System.out.println( "El detalle del usuario id: " + selectedUserId + " es:");
        System.out.println(response.prettyPrint());
    }
}
