package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.model.Category;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try
        {
            if(jwtFilter.isAdmin())
            {
                if(validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);

                }
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateid) {
        if(requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateid) {
                return true;
            }
            if (!validateid) {
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd){
        Category categoryObj = new Category();
        if(isAdd){
            categoryObj.setId(Integer.parseInt(requestMap.get("id")));
        }
        categoryObj.setName(requestMap.get("name"));
        return categoryObj;
    }
    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try
        {
            if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);


        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, true)){
                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty()){
                        categoryDao.save(getCategoryFromMap(requestMap, true));
                        return CafeUtils.getResponseEntity("Category Updated", HttpStatus.OK);
                    }
                    else {
                        return CafeUtils.getResponseEntity("Category id doesn't exist", HttpStatus.OK);
                    }

                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
