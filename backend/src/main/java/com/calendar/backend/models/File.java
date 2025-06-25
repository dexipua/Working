package com.calendar.backend.models;

import com.calendar.backend.models.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String path;
    private String fileName;
    private String fileSize;
    private String fileType;
    private String fileHash;
    private LocalDateTime uploadDate;
    private String fileRealName;
    private FileType fileTypeEnum;
}
