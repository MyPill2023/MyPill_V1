package com.mypill.domain.Image.repository;

import com.mypill.domain.Image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
