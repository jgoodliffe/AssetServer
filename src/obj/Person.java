package obj;

/**
 * Person
 * A 'Person' type of object within the database - this could be an employee, freelancer, client etc.
 */
public class Person {
    /*
        Fields
     */
    int id;
    int userLevel;
    boolean isEmployee;
    String name;
    String firstLine;
    String postCode;
    String email;
    String phone;
    String company;
    String role;
    String notes;
    long discountAmount;

    public Person(int userLevel, String name, String firstLine, String postCode, String email, String phone, String company, String role, String notes){
        this.name = name;
        this.firstLine = firstLine;
        this.phone = phone;
        this.postCode = postCode;
        this.email = email;
        this.company = company;
        this.role = role;
        this.notes = notes;
        this.userLevel = userLevel;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return firstLine + '\n' + postCode;
    }

    public String getPhone(){
        return phone;
    }

    public String getEmail(){
        return email;
    }

    public String getRole(){
        return role;
    }

    public String getCompany(){
        return company;
    }

    public int getDiscountLevel(){
        return Integer.parseInt(String.valueOf(discountAmount));
    }

    public int getUserLevel(){
        return userLevel;
    }

    public void changeUserLevel(int newLevel){
        userLevel = newLevel;
    }

    public void changeDiscountLevel(int newLevel){
        discountAmount = newLevel;
    }

    public void update(int userLevel, String name, String firstLine, String postCode, String email, String phone, String company, String role, String notes){
        this.name = name;
        this.firstLine = firstLine;
        this.phone = phone;
        this.postCode = postCode;
        this.email = email;
        this.company = company;
        this.role = role;
        this.notes = notes;
        this.userLevel = userLevel;
    }
}
