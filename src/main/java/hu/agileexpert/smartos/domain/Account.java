package hu.agileexpert.smartos.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String externalId;

	@Column(nullable = false, length = 80)
	private String name;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id")
	private Menu menu;

	@ManyToMany
	@JoinTable(
			name = "account_applications",
			joinColumns = @JoinColumn(name = "account_id"),
			inverseJoinColumns = @JoinColumn(name = "application_id")
	)
	private List<Application> applications = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theme_id")
	private Theme theme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallpaper_id")
	private Wallpaper wallpaper;
}
