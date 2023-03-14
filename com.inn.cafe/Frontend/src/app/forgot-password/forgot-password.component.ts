import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { SnackbarService } from '../services/snackbar.service';
import { UserService } from '../services/user.service';
import { GlobalConstants } from '../shared/global-constants';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordForm:any = FormGroup;
  responseMessage:any;


  constructor(private formBuilder: FormBuilder,
     private userServices:UserService,
      public dialogRef:MatDialogRef<ForgotPasswordComponent>,
      private snackBarService: SnackbarService) { }

  ngOnInit(): void {
    this.forgotPasswordForm = this.formBuilder.group({
      email: ["", [Validators.required, Validators.pattern(GlobalConstants.emailRegex)]]
    });
   
  }
  // this.userServices.signup(data).subscribe((response:any) => {

  handleSubmit(){
      var formData = this.forgotPasswordForm.value;
      var data = {
        email: formData.email
      }
      this.userServices.forgotPassword(data).subscribe((response:any) => {
        this.responseMessage = response?.message;
        this.dialogRef.close();
        this.snackBarService.openSnackBar(this.responseMessage, "");
      }, (error) => {
        if(error.error?.message){
          this.responseMessage = error.error?.message;
        }
        else{
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);

      })
     
  }
  

}
