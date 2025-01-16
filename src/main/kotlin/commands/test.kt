package net.hellz.commands

// Every kotlin function is public so it can be used anywhere

fun sayHello(){
    println("Hello!")
}

// Now the function is private so it can only be used here
private fun sayHellotoSomeone(name: String){
    println("Hello, $name!")
}

fun add() : Int{
    return 20 + 30
}

fun divide(x: Int, y:Int): Int = x / y

fun main(){
    println("Hello World")

    val myName: String = "Bek"// Value that cannot be changed

    var myAge: Int = 40 // Variable that can be changed

    val writer: String = "Chekhov"

    println(writer[0]) // Kotlin index starts at 0
    println(writer[1])

    println(writer.isEmpty())
    println(writer.length)


    // Conditionals

    if (myAge >= 18){
        println("You can smoke")

    } else {
        println("you cannot smoke yet")
    }


    // Collections

    val cities = mutableListOf<String>("Alexandria", "Barcelona", "Toronto", "Berlin")
    println(cities[0])

    cities.add("Rome")
    println(cities)

    cities.remove(cities[1])
    println(cities)

    for (city in cities){
        println(city)

    }
    for (i in 1..4){
        println(i)
    }

    for (i in 1 until 5){
        println(i)
    }

    // While Loop
    var index = 0
    while (index < cities.size){
        println("City at $index index is ${cities[index]}")
        index ++
    }

    sayHello()
    sayHellotoSomeone("John")

    println(add())

    println(divide(x=100, y=50))

    val data: String? = "Students Data Completed!"
    if (data != null){
        println(data.lowercase())
    }
    println(data?.uppercase())
}
