package manager;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;

import org.bson.Document;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.w3c.dom.html.HTMLTableColElement;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import model.Bodega;
import model.Campo;
import model.Entrada;
import model.Vid;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.SelfInjection.Split;
import utils.TipoVid;

public class Manager {
	
	private static Manager manager;
	private ArrayList<Entrada> entradas;
	private Session session;
	private Transaction tx;
	private Bodega b;
	private Campo c;
	
	MongoCollection<Document> collection;
	MongoDatabase database;

	private Manager () {
		this.entradas = new ArrayList<>();
	}
	
	public static Manager getInstance() {
		if (manager == null) {
			manager = new Manager();
		}
		return manager;
	}
	
	private void createSession() {
		String URL = "mongodb://localhost:27017";
		MongoClientURI mongoClientUrl = new MongoClientURI(URL);
		MongoClient mongoClient = new MongoClient(mongoClientUrl);
		database = mongoClient.getDatabase("dam2tm06uf2p2");
	}

	public void init() {
		createSession();
		getEntrada();
		manageActions();
		showAllCampos();
//		showCantidadVidByBodega();
//		session.close();
	}

	private void manageActions() {
		for (Entrada entrada : this.entradas) {
			try {
				System.out.println(entrada.getInstruccion());
				switch (entrada.getInstruccion().toUpperCase().split(" ")[0]) {
					case "B":
						addBodega(entrada.getInstruccion().split(" "));
						break;
					case "C":
						addCampo(entrada.getInstruccion().split(" "));
						break;
					case "V":
						addVid(entrada.getInstruccion().split(" "));
						break;
//					case "#":
//						vendimia();
//						break;
					default:
						System.out.println("Instruccion incorrecta");
				}
			} catch (HibernateException e) {
				e.printStackTrace();
				if (tx != null) {
					tx.rollback();
				}
			}
		}
	}

	private void vendimia() {
		this.b.getVids().addAll(this.c.getVids());
		this.b.setLlena(true);
		
		tx = session.beginTransaction();
		session.save(b);
		
		tx.commit();
	}

	private void addVid(String[] split) {
		collection = database.getCollection("campo");
		Document lastcampo = collection.find().sort(new Document("_id", -1)).first();
		
		collection = database.getCollection("vid");
		Document document = new Document().append("tipo", split[1]).append("cantidad", split[2]).append("campo", lastcampo);
		collection.insertOne(document);
		
		Document document2 = new Document().append("tipo", split[1]).append("cantidad", split[2]);
		
		collection = database.getCollection("campo");
		
		Document update = new Document("$push", new Document("vid", document2));
		collection.updateOne(lastcampo, update);
		
		
	}

	private void addCampo(String[] split) {
		collection = database.getCollection("bodega");
		Document lastBodega = collection.find().sort(new Document("_id", -1)).first();
		
		collection = database.getCollection("campo");
		Document document = new Document().append("name", lastBodega.getObjectId("_id")).append("collected", false).append("bodega", lastBodega);
		collection.insertOne(document);
	}

	private void addBodega(String[] strings) {
		collection = database.getCollection("bodega");
		Document document = new Document().append("name", strings[1]);
		collection.insertOne(document);
	}

	public void getEntrada() {
		collection = database.getCollection("entrada");
		
		for (Document document : collection.find()) {
			Entrada input = new Entrada();
			input.setInstruccion(document.getString("instruccion"));
			entradas.add(input);
		}
	}

	private void showAllCampos() {
		collection = database.getCollection("campo");
		
		for(Document document : collection.find()) {
			System.out.println(document);
		}
	}
	
	private void showCantidadVidByBodega() {
		tx = session.beginTransaction();
		Query q = session.createQuery("select v from Vid v group by bodega_id");
		List<Vid> list = q.list();
		for (Vid v : list) {
			System.out.println(v);
		}
		tx.commit();
		
	}
	


	
}
