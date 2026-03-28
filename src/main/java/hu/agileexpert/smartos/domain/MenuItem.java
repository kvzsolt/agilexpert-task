package hu.agileexpert.smartos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "menu_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MenuItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String uniqueIdentifier;

	@Column(nullable = false, length = 80)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	private Application application;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_menu_item_parent"))
	private MenuItem parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MenuItem> children = new ArrayList<>();
}
