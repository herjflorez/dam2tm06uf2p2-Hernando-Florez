package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "Entrada")
public class Entrada {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private String valor;

	@Column(name = "instruccion")
	private String instruccion;
	
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public void setInstruccion(String instruccion) {
		this.instruccion = instruccion;
	}

	public String getInstruccion() {
		return this.instruccion;
	}
}
