package com.example.board.config; // 또는 초기화 관련 적절한 패키지

import com.example.board.board.domain.Board;
import com.example.board.board.domain.Category;
import com.example.board.board.repository.BoardRepository;
import com.example.board.board.repository.CategoryRepository;
import com.example.board.user.domain.User; // User 엔티티 import
import com.example.board.user.repository.UserRepository; // UserRepository import
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 적용

import java.io.InputStream;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ObjectMapper objectMapper;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public DataInitializer(ObjectMapper objectMapper, BoardRepository boardRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.objectMapper = objectMapper;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 각 초기화 메소드 호출
        initializeBoards();
        initializeUsers();
        initializeCategory();
    }

    // 게시판 데이터 초기화 메소드
    private void initializeBoards() {
        if (boardRepository.count() == 0) {
            log.info("Initializing board data...");
            try {
                ClassPathResource resource = new ClassPathResource("data/boardData.json");
                InputStream inputStream = resource.getInputStream();
                List<Board> boardsToInitialize = objectMapper.readValue(inputStream, new TypeReference<List<Board>>() {});

                if (boardsToInitialize != null && !boardsToInitialize.isEmpty()) {
                    boardRepository.saveAll(boardsToInitialize);
                    log.info("Initialized board data: added {} boards.", boardsToInitialize.size());
                } else {
                     log.info("No board data found in JSON to initialize.");
                }
            } catch (Exception e) {
                log.error("Error during board data initialization.", e);
            }
        } else {
            log.info("Board data already exists. Skipping initialization.");
        }
    }

    // 사용자 데이터 초기화 메소드
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Initializing user data...");
            try {
                // 1. User 데이터용 JSON 파일 읽기
                ClassPathResource resource = new ClassPathResource("data/userData.json"); // 사용자 JSON 파일 경로
                InputStream inputStream = resource.getInputStream();

                // 2. JSON을 User 엔티티 리스트로 직접 변환
                // JSON 필드 이름과 User 엔티티 필드 이름 일치 가정 (userId 제외)
                List<User> usersToInitialize = objectMapper.readValue(inputStream, new TypeReference<List<User>>() {});

                // 3. 읽어온 User 엔티티 리스트를 DB에 저장
                if (usersToInitialize != null && !usersToInitialize.isEmpty()) {
                    userRepository.saveAll(usersToInitialize);
                    log.info("Initialized user data: added {} boards.", usersToInitialize.size());
                } else {
                    log.info("No user data found in JSON to initialize.");
                }

            } catch (Exception e) {
                log.error("Error during user data initialization.", e);
            }
        } else {
            log.info("User data already exists. Skipping initialization.");
        }
    }

    // 카테고리 데이터 초기화 메소드
    private void initializeCategory() {
        if (categoryRepository.count() == 0) {
            log.info("Initializing user data...");
            try {
                // 1. User 데이터용 JSON 파일 읽기
                ClassPathResource resource = new ClassPathResource("data/categoryData.json"); // 사용자 JSON 파일 경로
                InputStream inputStream = resource.getInputStream();

                // 2. JSON을 User 엔티티 리스트로 직접 변환
                // JSON 필드 이름과 User 엔티티 필드 이름 일치 가정 (userId 제외)
                List<Category> categoryToInitialize = objectMapper.readValue(inputStream, new TypeReference<List<Category>>() {});

                // 3. 읽어온 User 엔티티 리스트를 DB에 저장
                if (categoryToInitialize != null && !categoryToInitialize.isEmpty()) {
                    categoryRepository.saveAll(categoryToInitialize);
                    log.info("Initialized category data: added {} boards.", categoryToInitialize.size());
                } else {
                    log.info("No category data found in JSON to initialize.");
                }

            } catch (Exception e) {
                log.error("Error during category data initialization.", e);
            }
        } else {
            log.info("category data already exists. Skipping initialization.");
        }
    }
}