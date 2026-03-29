package hu.agileexpert.smartos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "wallpaper")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Wallpaper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String uniqueIdentifier;

	@Column(nullable = false, length = 120)
	private String name;

	@OneToMany(mappedBy = "wallpaper")
	@Builder.Default
	private List<Account> accounts = new ArrayList<>();
}
