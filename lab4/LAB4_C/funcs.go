package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

func checkRoute(graph [][]City, cities []City, rwLock *sync.RWMutex) {
	for true {
		rand.Seed(time.Now().UnixNano())

		var randFrom int
		var randTo int

		rwLock.RLock()
		for {
			randFrom = rand.Intn(len(graph))
			randTo = rand.Intn(len(graph))

			if randFrom != randTo {
				break
			}
		}

		price := dfs(graph, cities, randFrom, randTo)
		if price > 0 {
			println("Price from", randFrom, "to", randTo, "is", price)
		} else {
			println("There is no root between", randFrom, "and", randTo)
		}

		rwLock.RUnlock()
		time.Sleep(1 * time.Second)
	}
}

func dfs(graph [][]City, cities []City, from int, to int) int {
	visited := make([]bool, len(cities))
	return dfsHelper(graph, cities, &cities[from], &cities[to], visited)
}

func dfsHelper(adjList [][]City, cities []City, cur *City, dest *City, visited []bool) int {
	if cur.index == dest.index {
		return 0
	}
	visited[cur.index] = true
	for i := 0; i < len(adjList[cur.index]); i++ {
		neighbour := adjList[cur.index][i]
		if !visited[neighbour.index] {
			neighbourPrice := dfsHelper(adjList, cities, &neighbour, dest, visited)
			if neighbourPrice != -1 {
				return neighbourPrice + neighbour.routePrice
			}
		}
	}
	return -1
}

func changePrice(graph [][]City, rwLock *sync.RWMutex) {
	for true {
		rand.Seed(time.Now().UnixNano())

		rwLock.Lock()

		randFrom := rand.Intn(len(graph))
		rowLen := len(graph[randFrom])

		if rowLen != 0 {
			randTo := rand.Intn(rowLen)
			toCity := graph[randFrom][randTo]

			oldPrice := toCity.routePrice
			newPrice := 1 + rand.Intn(50)
			toCity.routePrice = newPrice

			for i := 0; i < len(graph[toCity.index]); i++ {
				tmpCity := graph[toCity.index][i]

				if tmpCity.index == randFrom {
					tmpCity.routePrice = newPrice
					break
				}
			}

			println(fmt.Sprintf("Changed price (%d) <-> (%d) from %d to %d",
				randFrom, toCity.index, oldPrice, newPrice))
		}

		rwLock.Unlock()
		time.Sleep(1 * time.Second)
	}
}

func changeRoute(graph [][]City, cities []City, rwLock *sync.RWMutex) {
	for true {
		rand.Seed(time.Now().UnixNano())

		rwLock.Lock()

		randFrom := rand.Intn(len(graph))
		rowLen := len(graph[randFrom])

		var opType Grades

		if rowLen == 0 {
			opType = EMPTY
		} else if rowLen == len(graph) {
			opType = FULL
		} else {
			opType = ANY
		}

		if opType == EMPTY || opType == ANY {
			exists := true
			var randTo int
			var newPrice int

			for exists {
				randTo = rand.Intn(len(cities))

				exists = false
				for i := 0; i < rowLen; i++ {
					if graph[randFrom][i].index == randTo {
						exists = true
						break
					}
				}

				if !exists {
					newPrice = 1 + rand.Intn(50)
					graph[randFrom] = append(graph[randFrom], City{randTo, newPrice})
				}

			}
			println(fmt.Sprintf("Added route (%d) <-> (%d) with price %d",
				randFrom, randTo, newPrice))
		} else {
			randToDelete := rand.Intn(rowLen)
			deleteCity := graph[randFrom][randToDelete]
			deleteIndex := deleteCity.index

			graph[randFrom] = append(graph[randFrom][:randToDelete], graph[randFrom][randToDelete+1:]...)

			for i := 0; i < len(graph[deleteIndex]); i++ {
				if graph[deleteIndex][i].index == randFrom {
					graph[deleteIndex] = append(graph[deleteIndex][:i], graph[deleteIndex][i+1:]...)
				}
			}

			println(fmt.Sprintf("Deleted route (%d) <-> (%d)",
				randFrom, deleteIndex))
		}

		rwLock.Unlock()
		time.Sleep(1 * time.Second)
	}
}

func changeCity(graph [][]City, rwLock *sync.RWMutex) {
	for true {
		rand.Seed(time.Now().UnixNano())
		randFrom := rand.Intn(len(graph))

		rwLock.Lock()

		graph[randFrom] = []City{}
		newCityRoads := graph[randFrom]

		for i := 0; i < len(graph); i++ {
			action := rand.Intn(2)%2 == 0

			if i != randFrom && action {
				newPrice := 1 + rand.Intn(50)
				newCityRoads = append(graph[randFrom], City{i, newPrice})
			}
		}

		for i := 0; i < len(graph); i++ {
			if i == randFrom {
				continue
			}

			row := graph[i]

			hasDeletedCity, dc := hasCity(row, randFrom)
			hasCurrentCity, cc := hasCity(newCityRoads, i)

			if hasDeletedCity && hasCurrentCity {
				row[dc].routePrice = newCityRoads[cc].routePrice
			} else if !hasDeletedCity && hasCurrentCity {
				row = append(row, City{randFrom, newCityRoads[cc].routePrice})
			} else if hasDeletedCity && !hasCurrentCity {
				row = append(row[:dc], row[dc+1:]...)
			}
		}

		println(fmt.Sprintf("Old city (%d) was deleted and created new one",
			randFrom))

		rwLock.Unlock()
		time.Sleep(5 * time.Second)
	}
}

func hasCity(row []City, index int) (bool, int) {
	for i := 0; i < len(row); i++ {
		if row[i].index == index {
			return true, i
		}
	}

	return false, -1
}
