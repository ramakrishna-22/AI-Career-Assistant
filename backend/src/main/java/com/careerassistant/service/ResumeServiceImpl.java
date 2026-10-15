package com.careerassistant.service;

import com.careerassistant.dto.ResumeResponse;
import com.careerassistant.entity.Resume;
import com.careerassistant.entity.User;
import com.careerassistant.exception.InvalidFileException;
import com.careerassistant.exception.ResourceNotFoundException;
import com.careerassistant.repository.ResumeRepository;
import com.careerassistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final Path uploadLocation;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".pdf", ".doc", ".docx");

    public ResumeServiceImpl(ResumeRepository resumeRepository,
                             UserRepository userRepository,
                             @Value("${app.resume.upload-dir:E:/AI-Career-Assistant/uploads/resumes}") String uploadDir) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location at " + uploadDir, e);
        }
    }

    @Override
    public ResumeResponse uploadResume(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Please select a valid file to upload");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new InvalidFileException("Invalid file name");
        }

        String cleanedFilename = Paths.get(originalFilename).getFileName().toString();
        String fileExtension = "";
        int dotIndex = cleanedFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            fileExtension = cleanedFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new InvalidFileException("Unsupported file format. Only .pdf, .doc, and .docx files are allowed");
        }

        // Prevent directory traversal and generate unique filename
        String storedFilename = UUID.randomUUID().toString() + "_" + cleanedFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path destinationFile = this.uploadLocation.resolve(storedFilename).normalize();

        if (!destinationFile.getParent().equals(this.uploadLocation)) {
            throw new InvalidFileException("Cannot store file outside current directory");
        }

        try {
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + cleanedFilename, e);
        }

        Resume resume = new Resume(
                user,
                cleanedFilename,
                file.getContentType() != null ? file.getContentType() : "application/octet-stream",
                file.getSize(),
                destinationFile.toString()
        );

        Resume savedResume = resumeRepository.save(resume);

        return mapToResponse(savedResume);
    }

    @Override
    public List<ResumeResponse> getUserResumes(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return resumeRepository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Resume getResumeEntity(Long resumeId, Long userId) {
        if (userId != null) {
            return resumeRepository.findByIdAndUserId(resumeId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId + " for user: " + userId));
        }
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));
    }

    @Override
    public Resource loadResumeAsResource(Long resumeId, Long userId) {
        Resume resume = getResumeEntity(resumeId, userId);
        try {
            Path filePath = Paths.get(resume.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Resume file not found on disk for id: " + resumeId);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File path is invalid for resume id: " + resumeId);
        }
    }

    private ResumeResponse mapToResponse(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                resume.getFileSize(),
                resume.getUploadedAt()
        );
    }
}
