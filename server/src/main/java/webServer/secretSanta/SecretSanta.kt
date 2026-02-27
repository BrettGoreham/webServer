package webServer.secretSanta

import kotlin.random.Random


fun prep(people: Array<Person>, blocked: Map<String, List<String>>) : List<Pair<Person, Person>> {
    var peopleMap: MutableMap<Person, List<Person>> = mutableMapOf()

    for ((k,v) in blocked) {
        peopleMap[personWithName(people, k)] = v.map { p -> personWithName(people, p) }
    }

    return findSecretSantaPlz(people, peopleMap);
}

fun findSecretSantaPlz(people: Array<Person>, blocked: Map<Person, List<Person>>) : List<Pair<Person, Person>>{
    val randomizedPeople = createAndRandomizeCopy(people).asList().toMutableList()

    canEveryoneMatch(randomizedPeople, blocked)
    ensureAtLeastOneValidSolution(randomizedPeople, blocked)

    var done = false
    var result: MutableList<Pair<Person, Person>> = mutableListOf()
    while(!done) {
        println("attempt")
        result = mutableListOf()
        val possibleChoices = createAndRandomizeCopy(people).asList().toMutableList()

        for (presentBuyer in randomizedPeople) {

            val isPossible = hasPotentialMatch(presentBuyer, blocked[presentBuyer], possibleChoices)
            if (!isPossible) {
                break
            }
            var matchFound = false
            while (!matchFound) {
                val index = Random.nextInt(0, possibleChoices.size)
                val potentialMatch = possibleChoices[index]
                if (potentialMatch != presentBuyer && blocked[presentBuyer]?.contains(potentialMatch) != true){
                    possibleChoices.removeAt(index)
                    result.add(Pair(presentBuyer, potentialMatch))
                    matchFound = true
                }
            }
        }

        if (result.size == people.size) {
            done = true
        }
    }

    return result
}

fun personWithName(people: Array<Person>, name: String) : Person {
    return people.first { p -> p.name == name };
}

fun createAndRandomizeCopy(people: Array<Person>) : Array<Person> {
    val people2 = people.copyOf()
    people2.shuffle()
    return people2
}

fun hasPotentialMatch(person:Person, blocked: List<Person>?, left: List<Person>) : Boolean {

    for (i in left) {
        if (i != person && (blocked == null || !blocked.contains(i))) {
            return true
        }
    }
    return false
}

fun canEveryoneMatch(people: List<Person>, blockedMap: Map<Person, List<Person>>) : Boolean {

    for (i in people) {
        if(!hasPotentialMatch(i, blockedMap[i], people)) {
            throw Exception("${i}s block list includes all other people")
        }
    }
    return true
}

fun ensureAtLeastOneValidSolution(people: MutableList<Person>, blockedMap: Map<Person, List<Person>>) : Boolean {
    val matched = recursive(people[0], people[0], people, blockedMap)

    if(!matched) {
        throw Exception("No circles possible in the graph")
    }

    return true
}

fun recursive(start: Person, person: Person, people: MutableList<Person>, blockedMap: Map<Person, List<Person>>): Boolean{

    if (people.size == 0){
        return true
    }

    for (p in people) {
        val blocked = blockedMap[person]
        if (p != person && (blocked == null || !blocked.contains(p))){
            val x = people.toMutableList()
            x.remove(p)

            val works: Boolean = if (x.size == 0) {
                return true
            } else if (p == start) {
                recursive(x[0], x[0], x, blockedMap)
            } else {
                recursive(start, p, x, blockedMap)
            }

            if (works) {
                return true
            }
        }
    }

    return false
}
