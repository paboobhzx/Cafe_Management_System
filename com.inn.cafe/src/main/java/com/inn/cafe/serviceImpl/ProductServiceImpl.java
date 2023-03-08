package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.model.Category;
import com.inn.cafe.model.Product;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl  implements ProductService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    ProductDao productDao;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, false)){
                    Product tempProduct = getProductFromMap(requestMap, false);
                    if(tempProduct.isValid()){
                        productDao.save(getProductFromMap(requestMap, false));
                        return CafeUtils.getResponseEntity("Product Added Successfully", HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId)
                return true;
            if(!validateId)
                return true;
        }
        return false;
    }
    //Added some minimal error checking
    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {

        Category categoryObj = new Category();
        Product productObj = new Product();
        String catId = requestMap.get("categoryId");
        if(!Strings.isNullOrEmpty(catId)){
            categoryObj.setId(Integer.parseInt(catId));
            if(isAdd){
                productObj.setId(Integer.parseInt(requestMap.get("id")));
            }
            else{
                productObj.setStatus("true");
            }
        }
        if(!Strings.isNullOrEmpty(requestMap.get("name")) && !Strings.isNullOrEmpty(requestMap.get("description"))){
            var price = requestMap.get("price");
            if(!Strings.isNullOrEmpty(price)){
                productObj.setCategory(categoryObj);
                productObj.setName(requestMap.get("name"));
                productObj.setDescription(requestMap.get("description"));
                productObj.setPrice(Integer.parseInt(requestMap.get("price")));
                return productObj;
            }
        }
        return new Product();
    }
    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try{
            return new ResponseEntity<>(productDao.getAllProducts(), HttpStatus.OK);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, true)){
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty())
                    {
                        Product productObj = getProductFromMap(requestMap, true);
                        productObj.setStatus(optional.get().getStatus());
                        productDao.save(productObj);
                        return CafeUtils.getResponseEntity("Product Updated Successfully", HttpStatus.OK);

                    }
                    else{
                        return CafeUtils.getResponseEntity("Product id does not exist", HttpStatus.OK);
                    }

                }
                else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional optional = productDao.findById(id);
                if(!optional.isEmpty()){
                    productDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Product Deleted", HttpStatus.OK);
                }
                else{
                    return CafeUtils.getResponseEntity("Product id doesn't exist", HttpStatus.OK);
                }

            }
            else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                var productId = requestMap.get("id");
                if(!Strings.isNullOrEmpty(productId)){
                    Optional optional = productDao.findById(Integer.parseInt(productId));
                    productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Product status updated", HttpStatus.OK);
                }
                else{
                    return CafeUtils.getResponseEntity("Product id doesn't exist", HttpStatus.OK);
                }

            }
            else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try{
            return new ResponseEntity<>(productDao.getProductByCategory(id), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try{
            return new ResponseEntity<>(productDao.getProductById(id), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
