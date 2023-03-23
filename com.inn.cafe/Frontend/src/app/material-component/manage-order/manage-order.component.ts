import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { saveAs } from 'file-saver';
import { BillService } from 'src/app/services/bill.service';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-manage-order',
  templateUrl: './manage-order.component.html',
  styleUrls: ['./manage-order.component.scss']
})
export class ManageOrderComponent implements OnInit {


  displayedColumns: string [] = ['name','category','price','quantity','total','edit'];
  dataSource:any = [];
  manageOrderForm: any = FormGroup;
  categories: any =  [];
  products: any = [];
  price:any;
  totalAmount:number = 0;
  responseMessage:any;

  constructor(private formBuilder:FormBuilder,
    private categoryService:CategoryService,
    private productService:ProductService,
    private snackbarService:SnackbarService,
    private billService:BillService) { }

  ngOnInit(): void {
    this.getCategories();
    this.manageOrderForm = this.formBuilder.group({
      name:["",[Validators.required]],
      email: ["",[Validators.required]],
      contactNumber: ["",[Validators.required]],
      paymentMethod: ["",[Validators.required]],
      product: ["",[Validators.required]],
      category: ["",[Validators.required]],
      quantity: ["",[Validators.required]],
      price: ["",[Validators.required]],
      total: [0,[Validators.required]]      
    });
  }
  getCategories(){
    this.categoryService.getFilteredCategories().subscribe((response:any) => {
      this.categories = response;
    }, (error:any) => {
      console.log(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      }else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage,GlobalConstants.error)
    })

  }
  getProductsByCategory(values: any){
    this.productService.getProductsByCategory(values.id).subscribe((response:any) => {
      this.products = response;
      this.manageOrderForm.controls['price'].setValue('');
      this.manageOrderForm.controls['quantity'].setValue('');
      this.manageOrderForm.controls['total'].setValue(0);
      

    }, (error: any) => {
      console.log(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      }else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })

  }
  getProductDetails(values:any){
    this.productService.getById(values.id).subscribe((response:any) => {
      this.price = response.price;
      this.manageOrderForm.controls['price'].setValue(response.price);
      this.manageOrderForm.controls['quantity'].setValue('1');
      this.manageOrderForm.controls['total'].setValue(this.price * 1);      
    }, (error:any) => {
      console.log(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  setQuantity(values:any){
    var temp = this.manageOrderForm.controls['quantity'].value;
    if(temp > 0){
      this.manageOrderForm.controls['total']
      .setValue(this.manageOrderForm.controls['quantity'].value * 
      this.manageOrderForm.controls['price'].value);
    } else if(temp != '') {
      this.manageOrderForm.controls['quantity'].setValue('1');
      this.manageOrderForm.controls['total'].setValue(this.manageOrderForm.controls['quantity'].value * this.manageOrderForm.controls['price'].value)

    }
  }

  validateProductAdd(){
    if(this.manageOrderForm.controls['total'].value === 0 ||
    this.manageOrderForm.controls['total'].value === null  ||
    this.manageOrderForm.controls['quantity'].value <= 0){
      return true;
    }
    else {
      return false;
    }
  }
  validateSubmit(){
    if(this.totalAmount === 0 || 
      this.manageOrderForm.controls['name'].value === null || 
      this.manageOrderForm.controls['email'] === null || 
      this.manageOrderForm.controls['contactNumber'].value === null || 
      this.manageOrderForm.controls['payment'] === null){
      return true;
    } else {
      return false;
    }
  }
  add(){
    console.log("inside manage-order add method");
    var formData = this.manageOrderForm.value;
    var productName = this.dataSource.find((e:{id:number}) => e.id === formData.product.id);
    if(productName === undefined){
      this.totalAmount = this.totalAmount + formData.total;
      this.dataSource.push({id:formData.product.id,
         name:formData.product.name,
          category:formData.category.name,
           quantity: formData.quantity,
            price: formData.price,
             total: formData.total});
      this.dataSource = [...this.dataSource];
      this.snackbarService.openSnackBar(GlobalConstants.productAdded, "success");
    } else {
      this.snackbarService.openSnackBar(GlobalConstants.productExistError, GlobalConstants.error);
    }
  }
  handleDeleteAction(value:any, element: any){
    this.totalAmount = this.totalAmount - element.total;
    this.dataSource.splice(value,1);
    this.dataSource = [...this.dataSource];
  }

  submitAction(){
    var formData = this.manageOrderForm.value;
    var data = {
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      paymentMethod: formData.paymentMethod,
      totalAmount: this.totalAmount.toString(),
      ProductDetails: JSON.stringify(this.dataSource)

    }
    this.billService.generateReport(data).subscribe((response:any) => {
      this.downloadFile(response?.uuid);
      this.manageOrderForm.reset();
      this.dataSource = [];
      this.totalAmount = 0;
    }, (error:any) => {
      console.log(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }
  downloadFile(fileName:string){
    var data = {
      uuid: fileName
    }

    this.billService.getPdf(data).subscribe((response: any) => {
      saveAs(response, fileName + '.pdf')
    })

  }

}
