# Problem Set 1

## Big O

### Problem 1: Exercise 3.7 of the textbook

1. Basic Operations:
    - Print star
    - Print space
    - Print new line

2. General Operation Count: 
    - `(n * (n + 1)) / 2` print stars
    - `(n * (n - 1)) / 2` print spaces
    - `n` print newlines 

3. `O(n) = n^2`

### Problem 4: Exercies 3.11 of the textbook

1. Simple Algorithm
```
SET commonSongs to []

FOR each song in list1 AS song1
    FOR each song in list2 AS song2
        if song1 = song2 THEN
            ADD song1 to commonSOngs
        ENDIF
    END
END

DISPLAY commonSongs
```
  - Worst case: `n * m`, no songs match
  - Best case: `n^2`, list1 exists in list2

2. Possible sorting
    - If the bigger list was sorted
        - Sorting using mergesort (`O(m log(m))`)
        - Using binary search (`O(nlog(m))`) 
        - Worst case: `O(nlog(m) + mlog(m))`, no songs match
        - Best case: `O(nlog(m) + mlog(m))`, list1 exists in list2

    - If both lists was sorted
        - Sorting using mergesort (`O(mlog(m) + nlog(n))`)
        - Using merge algorithm (`O(n + m)`) 
        - Worst case: `O(nlog(n) + mlog(m) + n + m)`, no songs match
        - Best case: `O(nlog(n) + mlog(m))`, list1 exists in list2

### Problem 5: Exercise 3.15 in the textbook
- ALGORITHMS (descriptions only):
    - Make array of sum of distances from start to every stop, then read and subtract distance from start of highway to 1st stop: `O(n)`
    - Sequential add from one stop to another: `O(n)`

