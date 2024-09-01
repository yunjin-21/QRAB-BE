package QRAB.QRAB.category.service;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.category.dto.*;
import QRAB.QRAB.category.repository.CategoryRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.excepiton.NotFoundMemberException;
import QRAB.QRAB.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service //db접근을 위한 repository와 사용자 인터페이스를 처리하는 controller 사이의 중간 계층 역할
@Transactional(readOnly = true)//데이터를 조회만 하기 - 읽기전용
@RequiredArgsConstructor// final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class CategoryService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = false)
    public ResponseEntity<?> saveCategory(CategoryRequestDTO categoryRequestDTO) {
        User user = userRepository.findOneWithAuthoritiesByUsername(categoryRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + categoryRequestDTO.getEmail()));
        boolean isDuplicate = categoryRepository.existsByName(categoryRequestDTO.getCategoryName());
        if(!isDuplicate){
            Category category = categoryRequestDTO.toEntity(user); //Category 객체 생성
            Category savedCategory = categoryRepository.save(category);// category 객체를 db에 저장
        }else{
            throw new RuntimeException("Could not add category: " + categoryRequestDTO.getCategoryName() + " is already exists");
        }
        return ResponseEntity.ok(categoryRequestDTO);
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> saveChildCategory(CategoryChildRequestDTO categoryChildRequestDTO){
        User user = userRepository.findOneWithAuthoritiesByUsername(categoryChildRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + categoryChildRequestDTO.getEmail()));
        boolean isDuplicate = categoryRepository.existsByName(categoryChildRequestDTO.getCategoryName());
        if(!isDuplicate){
            Category category = categoryChildRequestDTO.toEntity(user);//Category 객체 생성
            Category parentCategory = categoryRepository.findById(categoryChildRequestDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Could not find parent in category"));
            if(parentCategory.getParentCategory() == null){// 부모 카테고리의 parent id가 null일 경우만 (2계층 허용)
                category.setParentCategory(parentCategory); // 부모 카테고리를 자식 객체에 저장
            }else{
                throw new RuntimeException("Could not add parent in category");
            }
            Category savedCategory = categoryRepository.save(category);// category 객체를 db에 저장
        }else{
            throw new RuntimeException("Could not add category: " + categoryChildRequestDTO.getCategoryName() + " is already exists");
        }

        return ResponseEntity.ok(categoryChildRequestDTO);
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> updateCategory(CategoryUpdateDTO categoryUpdateDTO) {
        User user = userRepository.findOneWithAuthoritiesByUsername(categoryUpdateDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + categoryUpdateDTO.getEmail()));
        Category category = categoryRepository.findById(categoryUpdateDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Could not find category with ID"));

        boolean isDuplicate = categoryRepository.existsByNameAndIdNot(categoryUpdateDTO.getCategoryName(), categoryUpdateDTO.getCategoryId());

        if(!isDuplicate){
            category.setName(categoryUpdateDTO.getCategoryName());
            Category savedCategory = categoryRepository.save(category);// category 객체를 db에 저장
        }else{
            throw new RuntimeException("Could not add category: " + categoryUpdateDTO.getCategoryName() + " is already exists");
        }
        return ResponseEntity.ok(categoryUpdateDTO);
    }




    @Transactional(readOnly = false)
    public void deleteCategory(Long categoryId, String userEmail) throws Exception{ //자식 카테고리 삭제
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Could not find category ID"));
        if(!category.getUser().getUsername().equals(userEmail)){
            throw new RuntimeException("Don't have permission to delete category");
        }
        List<Category> categoryChildList = categoryRepository.findByParentCategory(category);
        //만약 category 가 부모일 경우 findByParentCategory 사용해서 해당 부모 카테고리의 자식 카테고리들을 포함하는 List<Category> - 허용 x
        //만약 categroy 가 자식일 경우 - 허용 ok
        if(categoryChildList.isEmpty()){
            categoryRepository.delete(category);
        }else{
                throw new RuntimeException("Category cannot be deleted");//자식 카테고리 존재해서 부모 카테고리 삭제 불가
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryParentResponseDTO> getUserParentCategories(String username){
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));

        List<Category> categories = categoryRepository.findByUser(user);
        List<CategoryParentResponseDTO> categoryResponseDTOS = categories.stream()
                .map(CategoryParentResponseDTO::fromEntity)
                .filter(Objects::nonNull) // 필터링하여 null 값 제거
                .collect(Collectors.toList()); // 카테고리 엔티티 객체로부터 DTO로 변환하여 반환
        return categoryResponseDTOS;
    }
    @Transactional(readOnly = true)
    public List<CategoryChildResponseDTO> getUserChildCategories(String username){
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));

        List<Category> categories = categoryRepository.findByUser(user);
        List<CategoryChildResponseDTO> categoryResponseDTOS = categories.stream()
                .map(CategoryChildResponseDTO::fromEntity)
                .filter(Objects::nonNull) //필터링해 null 값 제거
                .collect(Collectors.toList()); // 카테고리 엔티티 객체로부터 DTO로 변환하여 반환
        return categoryResponseDTOS;
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> getParentCategories(){
        List<Category> categories = categoryRepository.findTopLevelCategories();
        List<CategoryResponseDTO> categoryResponseDTOS = categories.stream()
                .map(CategoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryResponseDTOS);
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> getChildWithParent(Long parentId){
        List<Category> parentCategories = categoryRepository.findTopLevelCategories();
        List<CategoryResponseDTO> categoriesParentResponseDTOS = parentCategories.stream()
                .map(CategoryResponseDTO::fromEntity)
                .collect(Collectors.toList());

        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Could not find parentId"));


        List<Category> childCategories = categoryRepository.findByParentCategory(parentCategory);
        List<CategoryResponseDTO> categoriesChildResponseDTOS = childCategories.stream()
                .map(CategoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("parentCategories", categoriesParentResponseDTOS);
        result.put("childCategories", categoriesChildResponseDTOS);
        return ResponseEntity.ok(result);

    }


}
