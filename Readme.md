#Agente Inteligente para Angry Birds.

Solución propuesta para la competencia [aibirds](www.aibirds.org).

##Log de cambios:
* **08 May 2015:**

  Agregado funciones estáticas en la clase Building.java, para la detección de cuáles son todas las construcciones y cuáles son las construcciones con chanchos ( por ahora solo se fija si el chancho está dentro de un rectángulo que simboliza la construcción, no toma el contorno real, por lo que puede ser que el chacho esté sobre la construcción, y no dentro.) 

  Agregadas unas correcciones en SceneState.java, ABObject.java y ClientNaive.java para usar Building.
  Por ahora podemos usarlas asi... 
  ```java
  // Código sacado del naive
  
  this.Scene.Buildings = Building.FindBuildings(this.Scene.Blocks); //Construcciones
  this.Scene.Buildings = Building.FindBuildings(this.Scene.Blocks, this.Scene.Pigs); // Construcciones con chanchos
  ```
* **07 May 2015:**
  
  Agregado el modulo y la clase:
  * dl.heuristics
    * dl.heuristics.SceneState.java

  Desde el proyecto de DataLab. 
  Esta clase la usaríamos para ir guardando todos los objetos que aparezcan en la escena.
  También se modificó la clase ClientNaiveAgent.java Implementando una instancia de la clase anterior.
* **04 May 2015:**
  
  Agregadas la clase ABObjectComp desde el proyecto de Datalab para poder comparar los objetos ABObjec
  Lo mas seguro es que lo usemos para ordenar las listas de ABObjects.
  ```java
  // Ej:

  ABObjectComp comparator = new ABObjectComp();
  comparator.sortByWidth();
  comparator.sortDesc();
  Collections.sort(pigs, comparator);
  ```
* **30 Abr 2015:**
  
  Se agregó un sort para los chancos y se hizo que dispare primero al más grande.
* **27 Abr 2015:**

  Version original del Framework, pero con el ABObject.java y el ABType.java del DATALAB para aprovechar los metodos de deteccion de objetos.
