package hu.agileexpert.smartos.repository;

import hu.agileexpert.smartos.domain.Wallpaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WallpaperRepository extends JpaRepository<Wallpaper, Long> {
}

