export class GlobalConstants {
    //Message
    public static productExistError:string = "Product already exists";
    public static productAdded:string = "Product added successfully";
    public static genericError: string = "Something went wrong. Please try again later"

    public static unauthorizedMessage : string = "You're not authorized to access this resource";

    //regex
    public static nameRegex: string = "Name [a-zA-Z0-9 ]*";
    public static emailRegex: string = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$"  ;
    public static contactNumberRegex: string = "^[e0-9]{10,10}$";

    //Variable
    public static error:string  = "error";
}