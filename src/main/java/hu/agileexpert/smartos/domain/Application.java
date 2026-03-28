package hu.agileexpert.smartos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String uniqueIdentifier;

	@Column(nullable = false, length = 80)
	private String name;

	@ManyToMany(mappedBy = "applications")
	@Builder.Default
	private List<Account> accounts = new ArrayList<>();
}
