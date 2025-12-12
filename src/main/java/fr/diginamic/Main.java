package fr.diginamic;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Properties props = new Properties();
        InputStream input = Main.class
                .getClassLoader()
                .getResourceAsStream("application.properties");
        props.load(input);
        String host = props.getProperty("mongodb.host");
        String dbName = props.getProperty("mongodb.database");
        String collName = props.getProperty("mongodb.collection");
        String uri = "mongodb://" + host + "/";
        MongoManager mongoManager = new MongoManager(uri, dbName, collName);
        // Utilisation


        // A. Insérer des document ///////////////////////////////////////////////////////

        Document document = new Document("name","fruit du dragons")
                .append("category","fruit")
                .append("color","yellow")
                .append("price",10.0)
                .append("quantity",20);
        Map<String, Object> createdOne = mongoManager.createOneDocument(document);
        System.out.println(("create_one_document: " + createdOne));

        List<Document> listDocument = List.of(new Document("name","mangue")
                .append("category","fruit")
                .append("color","yellow")
                .append("price",10.0)
                .append("quantity",20)
                ,new Document("name","poivron")
                .append("category","legumes")
                        .append("color","yellow")
                        .append("price",10.0)
                        .append("quantity",20));
        Map<String, Object> createdMany = mongoManager.createManyDocuments(listDocument);
        System.out.println(("create_many_document: " + createdMany));

        ///////////////////////////////////////////////////////////////////////////////////

        // B Mettre à jour des documents /////////////////////////////////////////////////

        // B.1  Mettez à jour le prix et la quantité d'un produit spécifique /////////////

        Map<String,Object> updateedOne = mongoManager.updateOneDocument(new Document("name","poivron"),new Document("$set", new Document("price",2.500).append("quantity",2)));
        System.out.println("update_one_document: " + updateedOne);


        // B.2  Ajoutez une nouvelle propriété à un produit existant. ///////////////////

        Map<String,Object> ajouterProrieter = mongoManager.updateOneDocument(new Document("name","poivron"),new Document("$set", new Document("Aciditer",false)));
        System.out.println("update_one_document: " + ajouterProrieter);

        // B.3 Supprimez une propriété d'un produit existant. ///////////////////////////

        Map<String,Object> supprimerProprieter = mongoManager.updateOneDocument(new Document("name","poivron"),new Document("$unset", new Document("Aciditer",false)));
        System.out.println("update_one_document: " + supprimerProprieter);

        // C. Manipuler des tableaux /////////////////////////////////////////////////////

        // C.1  Ajoutez un élément à un tableau. Exemple : 'alternative_colors' égal à 'Green'//////////////////////
        Map<String,Object> tableau = mongoManager.updateOneDocument(new Document("category","Fruit"),new Document("$push",new Document("type","pomme")));
        System.out.println("update_one_document: " + tableau);

        // C.2 Ajoutez plusieurs éléments à un tableau. /////////////////////////////////////////////////////////////
        List<String> stringList = List.of("pomme","orange","cerise");
        Map<String,Object> tableauMany = mongoManager.updateOneDocument(new Document("category","Fruit"),new Document("$push",new Document("type",new Document("$each",stringList))));
        System.out.println("update_one_document: " + tableauMany);

        // C.3  Supprimez un élément d'un tableau. /////////////////////////////////////////////////////////////////
        Map<String,Object> supprimerElementTab = mongoManager.updateOneDocument(new Document("category","Fruit"),new Document("$pull",new Document("type","pomme")));
        System.out.println("update_one_document: " + supprimerElementTab);

        // C.4  Supprimez le dernier élément d'un tableau. /////////////////////////////////////////////////////////
        Map<String,Object> supprimerDernierElementTab = mongoManager.updateOneDocument(new Document("category","Fruit"),new Document("$pop",new Document("type",1)));
        System.out.println("update_one_document: " + supprimerDernierElementTab);

        // D Supprimer des documents /////////////////////////////////////////////////////////////////////////////////

        // D.1 Supprimez un fruit spécifique grâce à son _id de la collection products.//////////////////////////////
        Map<String,Object> supprimerDocument = mongoManager.deleteOneDocument(new Document("_id",new ObjectId("693c132285a7f493efbdbc5f")));
        System.out.println("DELETE_one_document: " + supprimerDocument);

        // D.2 Supprimer tous les fruits et légumes qui sont Green. ////////////////////////////////////////////////
        Map<String,Object> supprimerDocumentMany = mongoManager.deleteManyDocuments(new Document("color","Green"));
        System.out.println("DELETE_one_document: " + supprimerDocumentMany);

        // E Requêtes de recherche ////////////////////////////////////////////////////////////////////////////////

        // E.1  Recherchez tous les produits de couleur rouge./////////////////////////////////////////////////////
        List<Document> recherche1 = mongoManager.readManyDocuments(new Document("color","Red"));
        for (Document r : recherche1)
        {
            System.out.println(r);
        }

        // E.2  Recherchez tous les produits dont le prix est inférieur à 2.00 /////////////////////////////////////
        List<Document> recherche2 = mongoManager.readManyDocuments(new Document("price",new Document("$lt",2.00)));
        for (Document r : recherche2)
        {
            System.out.println(r);
        }

        // E.3  Recherchez le fruit qui à la plus grande quantité. //////////////////////////////////////////////
        List<Document> recherche3 = mongoManager.readManyDocuments(new Document("category","Fruit"),1,new Document("quantity",-1));
        System.out.println(recherche3);
        mongoManager.closeConnection();
    }
}
