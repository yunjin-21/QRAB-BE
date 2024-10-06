package QRAB.QRAB.category.controller;

import QRAB.QRAB.category.dto.CategoryChildRequestDTO;
import QRAB.QRAB.category.dto.CategoryRequestDTO;
import QRAB.QRAB.category.dto.CategoryUpdateDTO;
import QRAB.QRAB.category.service.CategoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/categories")
@RestController //http 요청을 처리하고 json 형식으로 데이터를 반환 - restful web service  @controller와 @responseBody를 함께 사용
@RequiredArgsConstructor // final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        categoryRequestDTO.setEmail(authentication.getName());
        return categoryService.saveCategory(categoryRequestDTO);
    }
    @PostMapping("/child")
    public ResponseEntity<?> addChildCategory(@RequestBody CategoryChildRequestDTO categoryChildRequestDTO) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        categoryChildRequestDTO.setEmail(authentication.getName());
        return categoryService.saveChildCategory(categoryChildRequestDTO);
    }

    @PutMapping("/update") //부모 + 자식 모두 업데이트
    public ResponseEntity<?> updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO) throws IOException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        categoryUpdateDTO.setEmail(authentication.getName());
        return categoryService.updateCategory(categoryUpdateDTO);
    }



    @DeleteMapping("/{categoryId}") //category 삭제는 자식만 가능 + 자식이 없을 경우 부모도 삭제가능
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryId")Long categoryId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            categoryService.deleteCategory(categoryId, authentication.getName());
            return ResponseEntity.ok("Category is deleted successfully");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 상위 카테고리 조회
    @GetMapping("/parent")//카테고리 조회 페이지 - parent id + parent name 만 보이게
    public ResponseEntity<?> getParentCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return categoryService.getParentCategories(username);
    }

    //상위 카테고리 + 해당 상위 카테고리의 하위 카테고리 조회
    @GetMapping("/parent/{parentId}/child")
    public ResponseEntity<?> getChildWithParent(@PathVariable("parentId") Long parentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return categoryService.getChildWithParent(parentId, username);
    }


}
