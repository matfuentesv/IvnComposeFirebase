package cl.smartsolutions.ivnapp.model

data class User(
    private var rut: String,
    private var firstName: String,
    private var lastName: String,
    private var email: String,
    private var password: String,
    private var age: Int) {

    fun getRut(): String {
        return rut;
    }

    fun getEmail(): String {
        return email
    }

    fun getPassword(): String {
        return password
    }

    fun getFirstName(): String {
        return firstName
    }

    fun getLastName(): String {
        return lastName
    }

    fun getAge(): Int {
        return age
    }

    fun setRut(rut:String) {
        this.rut = rut;
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun setAge(age: Int) {
        this.age = age
    }


}