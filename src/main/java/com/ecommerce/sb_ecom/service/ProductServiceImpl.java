package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.exceptions.APIExceptions;
import com.ecommerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.repository.CategoryRepository;
import com.ecommerce.sb_ecom.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;




@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;
    private String sortOrder;


    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        //Check if product already present or not

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();
        for(int i=0;i<products.size();i++){
            if(products.get(i).getProductName().equals(productDTO.getProductName())){
                 isProductNotPresent = false;
                 break;
            }
        }
        if(isProductNotPresent) {


            Product product = modelMapper.map(productDTO, Product.class);

            // Optional: only keep if you have "image" field in Product.java
            product.setImage("default.png");

            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);

            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        } else{
             throw new APIExceptions("Product already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
         Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                 ?Sort.by(sortBy).ascending()
                 :Sort.by(sortBy).descending();
        Pageable pagedetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pagedetails);


        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());





       if(products.isEmpty()){
           throw new APIExceptions("No Products Exist!!!");
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //product size is 0
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pagedetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category,pagedetails);


        List<Product> products = pageProducts.getContent();
        if(products.isEmpty()){
            throw new APIExceptions(category.getCategoryName() +"category does not have any products");
        }



        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String s, Integer pageNumber, Integer pageSize, String sortBy, String keyword) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pagedetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Product> pageProducts = (Page<Product>) productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%");





        List<Product> products = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%");


        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        if(products.isEmpty()){
            throw new APIExceptions("Products not found with keyword:" +keyword);
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        //get the existing products from database
        Product product = modelMapper.map(productDTO, Product.class);
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        // update the product info with user shared
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        //Save to the database
        Product savedProduct = productRepository.save(productFromDb);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(product);

        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //get the product from database
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        //upload the image to the server
        //get the file name of uploaded image

        String fileName = fileService.uploadImage(path,image);



        //updating the new file name to the product
        productFromDb.setImage(fileName);
        //save the updated product as well
        Product updatedProduct = productRepository.save(productFromDb);
        // return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);



    }






}








