package sample.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data

@Entity
@Table(name="user")

public class User {
	@Id
	private String id;
	@NotNull
	private int request;
	@NotNull
	private int seconds;
}
