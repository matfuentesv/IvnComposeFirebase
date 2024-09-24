package cl.smartsolutions.ivnapp.model

data class User(
    private var firstName: String,
    private var lastName: String,
    private var email: String,
    private var password: String,
    private var age: Int) {

    // Getters
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

    // Setters
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

    // Método para mostrar la información del usuario
    fun getUserInfo(): String {
        return "User(email='$email', password='$password', firstName='$firstName', lastName='$lastName', age=$age)"
    }

}