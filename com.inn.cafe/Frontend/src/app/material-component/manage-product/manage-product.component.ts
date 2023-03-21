import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';
import { ConfirmationComponent } from '../confirmation/confirmation.component';
import { ProductComponent } from '../dialog/product/product.component';

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss']
})
export class ManageProductComponent implements OnInit {

  displayedColumns: string[] = ['name', 'categoryName', 'description', 'price', 'edit'];
  dataSource: any;
  //length: any;
  responseMessage: any;

  constructor(private productService: ProductService,
    private dialog: MatDialog,
    private snackBarService: SnackbarService,
    private router: Router) { }

  ngOnInit(): void {
    this.tableData();

  }

  tableData() {
    this.productService.getProducts().subscribe((response: any) => {
      this.dataSource = new MatTableDataSource(response);

    }, (error: any) => {
      console.log(error.error?.message);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      }
      else {
        this.responseMessage = GlobalConstants.genericError;
      }
    })

  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  handleAddAction() {
    console.log("Inside handleAddAction")
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      action: 'Add'
    };
    dialogConfig.width = "850px";
    const dialogRef = this.dialog.open(ProductComponent, dialogConfig);
    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    const sub = dialogRef.componentInstance.onAddProduct.subscribe((response) => {
      this.tableData();
    })

  }
  handleEditAction(values: any) {
    console.log("Inside product handleEditAction")
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      action: 'Edit',
      data: values
    };
    dialogConfig.width = "850px";
    const dialogRef = this.dialog.open(ProductComponent, dialogConfig);
    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    const sub = dialogRef.componentInstance.onEditProduct.subscribe((response) => {
      this.tableData();
    })


  }
  handleDeleteAction(values: any) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
     
      action: 'Delete',
      message: 'delete ' + values.name,
      confirmation: true
    }
    const dialogRef = this.dialog.open(ConfirmationComponent, dialogConfig);
    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe((response) => {
      this.deleteProduct(values.id);
      dialogRef.close();

    })
    
   


  }
  deleteProduct(id: any) {
    this.productService.delete(id).subscribe((response: any) => {
      this.tableData();
      this.responseMessage = response?.message;
      this.snackBarService.openSnackBar(this.responseMessage, "success");
    }, (error: any) => {
      console.log(error.error?.message);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })

  }
  onChange(status: any, id: any) {
    var data = {
      status: status.toString(),
      id: id
    }
    this.productService.updateStatus(data).subscribe((response: any) => {
      this.responseMessage = response?.message;
      this.snackBarService.openSnackBar(this.responseMessage, "success")
    }, (error: any) => {
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })

  }

}
