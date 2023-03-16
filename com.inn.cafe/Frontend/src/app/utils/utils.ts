export default class Utils {
    public showPassword: boolean = false;
    constructor() { }
    public togglePasswordVisibility(): void {
        this.showPassword = !this.showPassword;
      }


}