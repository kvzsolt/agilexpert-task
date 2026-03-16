package hu.agileexpert.smartos.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String externalId;

	@Column(nullable = false, length = 80)
	private String name;

	@OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MenuItem> menuItems = new ArrayList<>();

	@OneToOne(mappedBy = "menu")
	private Account owner;
}
