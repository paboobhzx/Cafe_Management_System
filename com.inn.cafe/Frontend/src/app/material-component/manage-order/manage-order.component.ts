import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
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
      email: ["",Validators.required,Validators.pattern(GlobalConstants.emailRegex)],
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

}
