package com.ecommerce.sb_ecom.controller;


import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {


    @Autowired
    ProductService productService;
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId){

       ProductDTO savedproductDTO = productService.addProduct(categoryId,productDTO);
       return  new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){
       ProductResponse productResponse= productService.getAllProducts();
       return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }


    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId){
      ProductResponse productResponse = productService.searchByCategory(categoryId);
      return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword){
      ProductResponse productResponse =productService.searchProductByKeyword(keyword);
      return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

     @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO,@PathVariable Long productId){

        ProductDTO updatedProductDTO = productService.updateProduct(productId,productDTO);
         return new ResponseEntity<>(updatedProductDTO,HttpStatus.OK);
    }
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO deletedProduct =productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

}
