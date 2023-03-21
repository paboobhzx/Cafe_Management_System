import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { CategoryService } from 'src/app/services/category.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import {Router } from '@angular/router'
import { MatTableDataSource } from '@angular/material/table';
import { GlobalConstants } from 'src/app/shared/global-constants';
import { CategoryComponent } from 'src/app/category/category.component';

@Component({
  selector: 'app-manage-category',
  templateUrl: './manage-category.component.html',
  styleUrls: ['./manage-category.component.scss']
})
export class ManageCategoryComponent implements OnInit {
  displayedColumns: string[] = ['name','edit'];
  dataSource: any;
  responseMessage: any

  constructor(private categoryService: CategoryService,
    private dialog:MatDialog,
    private snacbarService:SnackbarService,
    private router:Router) { }

  
    ngOnInit(): void {
      this.tableData();

  }
  tableData(){
    this.categoryService.getCategories().subscribe((response:any) => {
      this.dataSource = new MatTableDataSource(response);
    }, (error:any) => {
      console.log(error.error?.message)
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snacbarService.openSnackBar(this.responseMessage,GlobalConstants.error);
    })

  }
  applyFilter(event:Event){
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  handleAddAction(){
    
    const dialogConfig = new MatDialogConfig();    
    dialogConfig.data = {
      action: 'Add'
    };
    dialogConfig.width = "850px";
    const dialogRef = this.dialog.open(CategoryComponent, dialogConfig);
    this.router.events.subscribe(() => {
      dialogRef.close();
    });
    const sub = dialogRef.componentInstance.onAddCategory.subscribe((response) => {
      this.tableData();
    })  
  }


  handleEditAction(values:any){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      action: 'Edit',
      data:values
    };
    dialogConfig.width = "850px";
    const dialogRef = this.dialog.open(CategoryComponent, dialogConfig);
    this.router.events.subscribe(() => {
      dialogRef.close();
    });
    const sub = dialogRef.componentInstance.onEditCategory.subscribe((response) => {
      this.tableData();
    })  
  }
  

  }


