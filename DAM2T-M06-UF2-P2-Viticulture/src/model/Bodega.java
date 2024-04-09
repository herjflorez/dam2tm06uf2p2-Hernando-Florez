package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name= "bodega")
public class Bodega {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = true)
	private int id_bodega;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "llena")
	private boolean llena;
	
	@OneToMany
    @JoinColumn(name = "bodega_id")
	private List<Vid> vids;
	
	public Bodega() {}
	
	public Bodega(String nombre) {
		this.nombre = nombre;
		this.vids = new ArrayList<>();
		this.llena = false;
	}

	@Override
	public String toString() {
		return "Bodega [id_bodega=" + id_bodega + ", vids=" + Arrays.toString(vids.toArray()) + "]";
	}

	public List<Vid> getVids() {
		return this.vids;
	}

	public boolean isLlena() {
		return llena;
	}

	public void setLlena(boolean llena) {
		this.llena = llena;
	}
	
	
	
}
