import { Component, EventEmitter, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.scss']
})
export class ProductComponent implements OnInit {

  onAddProduct = new EventEmitter();
  onEditProduct = new EventEmitter();
  productForm: any = FormGroup;
  dialogAction: any = "Add";
  action: any = "Add";
  responseMessage: any;
  categories: any = [];
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any,
    private formBuilder: FormBuilder,
    private productService: ProductService,
    public dialogRef: MatDialogRef<ProductComponent>,
    private categoryService: CategoryService,
    private snackbarservice: SnackbarService) { }

  ngOnInit(): void {
    this.productForm = this.formBuilder.group({
      name: ["", [Validators.required]],
      categoryId: ["", [Validators.required]],
      price: ["", [Validators.required]],
      description: ["", Validators.required]
    });
    console.log("Inside ngOnInit product.component.ts\n" + this.dialogData.action )
    if (this.dialogData.action === "Edit") {
      this.dialogAction = "Edit"
      this.action = "Update"      
      this.productForm.patchValue(this.dialogData.data);
      console.log("DialogData.data after patchValue:\n " + this.dialogData.data)
    }
    this.getCategories();

  }
  getCategories() {
    this.categoryService.getCategories().subscribe((response: any) => {
      this.categories = response;
    }, (error: any) => {
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarservice.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  handleSubmit() {    
    console.log("this.dialogActio.action value " + this.dialogAction.action)
    if (this.dialogAction.action === "Edit") {
      this.edit();
    }
    else {
      this.add();
    }
  }

  add() {
    var formData = this.productForm.value;
    var data = {
      name: formData.name,
      categoryId: formData.categoryId,
      price: formData.price,
      description: formData.description
    }
    this.productService.add(data).subscribe((response: any) => {
      //console.log("Form data: " + "\n" + formData);
      this.dialogRef.close();
      this.onAddProduct.emit();
      this.responseMessage = response.message;
      this.snackbarservice.openSnackBar(this.responseMessage, "success");
    }, (error: any) => {
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarservice.openSnackBar(this.responseMessage, GlobalConstants.error);
    })

  }
  edit() {

    var formData = this.productForm.value;
    var data = {
      id: this.dialogData.data.id,
      name: formData.name,
      categoryId: formData.categoryId,
      price: formData.price,
      description: formData.description
    }
    this.productService.update(data).subscribe((response: any) => {
      this.dialogRef.close();
      this.onEditProduct.emit();
      this.responseMessage = response.message;
      this.snackbarservice.openSnackBar(this.responseMessage, "success");
    }, (error: any) => {
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarservice.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }
  
}

