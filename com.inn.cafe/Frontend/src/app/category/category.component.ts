import { Component, Inject, OnInit } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CategoryService } from '../services/category.service';
import { SnackbarService } from '../services/snackbar.service';
import { GlobalConstants } from '../shared/global-constants';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit {

  onAddCategory = new EventEmitter();
  onEditCategory = new EventEmitter();
  categoryForm:any = FormGroup;
  dialogAction:any = "Add"
  action:any = "Add";

  responseMessage: any;
  
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData:any,
  private formBuilder:FormBuilder,
  private categoryService:CategoryService,
  public dialogRef: MatDialogRef<CategoryComponent>,
  private snackbarservice:SnackbarService
  ) { }

  ngOnInit(): void {
    console.log("Dialog action inside constructor " + this.dialogAction);
    this.categoryForm = this.formBuilder.group({
      name:["",[Validators.required]],
      
    });

    
    if(this.dialogData.action === 'Edit'){
      this.dialogAction = "Edit";
      this.action = "Update";
      this.categoryForm.patchValue(this.dialogData.data);
    }
  }

  handleSubmit(){        
    
    if(this.dialogAction === "Edit"){
      this.edit();

    }else{
      this.add();

    }
  }

  add(){
    var formData = this.categoryForm.value;
    var data = {
      name: formData.name
    }
    this.categoryService.add(data).subscribe((response:any) =>{
      this.dialogRef.close();
      this.onAddCategory.emit();
      this.responseMessage = response.message;
      this.snackbarservice.openSnackBar(this.responseMessage, "success");
    }, (error) => {
      this.dialogRef.close();
      console.error(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      }else
      {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarservice.openSnackBar(this.responseMessage,GlobalConstants.error);
    });

  }

  edit(){

    var formData = this.categoryForm.value;
    var data = {
      id: this.dialogData.data.id,
      name: formData.name
    }
    this.categoryService.update(data).subscribe((response:any) =>{
      this.dialogRef.close();
      this.onEditCategory.emit();
      this.responseMessage = response.message;
      this.snackbarservice.openSnackBar(this.responseMessage, "success");
    }, (error) => {
      this.dialogRef.close();
      console.error(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      }else
      {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarservice.openSnackBar(this.responseMessage,GlobalConstants.error);
    });

  }

}
