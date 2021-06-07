# Requerimientos TNT:

- Carga inicial del proyecto:
  El commit inicial debe tener configurado:
  - Librerías:
    - DataBinding
    - androidx.navigation
  - Componentes:
    - Grafo de navegación
    - MainActivity con NavHostFragment
- Configurar Firebase -> agregar proyecto. Ej:

  ```
  {
    tiendas: [
      {
        id: '#1208765',
        nombre,
        rubro,
        ubicación,
        horario-de-atención,
        métodos-de-pago-que-acepta,
      },
      {...},
    ],

    productos: [
      {
        id: '#1125435',
        nombre,
        cantidad-disponible,
        precio-unitario,
        categoría,
        fotografía,
        observaciones
      },
      {...}
    ],
    pedidos: [
      {
        id: '#98765',
        productos: [ listado de ids? ],
        estado: ['creado', 'en-curso', 'preparado', 'entregado'],
        estampa-de-tiempo: '2021-06-21-17:32:21'
      }

      usuarios?
      vendedores?
    ]

  }
  ```

## Funcionalidad para vendedores:

- [ ] Inicio sesión.
      Validar usuario y contraseña -> Pasar a la pantalla HOME

- [ ] Alta su tienda
      Crear formulario:

      ```
      - nombre,
      - rubro,
      - ubicación,
      - horario de atención,
      - métodos de pago que acepta.
      ```

  Cuando damos click en crear -> Guardar TIENDA en Firebase

- [ ] Carga productos:

  1. Pantalla de carga:

     - nombre,
     - cantidad disponible,
     - precio unitario,
     - una categoría,
     - una o más fotografías,
     - campo de observaciones o información complementario.
     - (Dividir en varias pantallas?)

  2. Cuando damos click en crear -> Guardar PRODUCTO en Firebase

- [ ] Manejo de pedidos:

  1. Notificaciones llegada de pedidos -> Firebase notifications
  2. Listado de pedidos.
     - Listado pedidos bandeja de entrada.
     - Listado pedidos en curso.
     - Listado pedidos preparado.
     - Listado pedidos entregados (historicos).

## Funcionalidad para compradores:

La aplicación debe permitirle a los vendedores:

[ ] - Inicio de sesión:

1. Crear formulario:

   - nombre,
   - usuario y
   - contraseña
   - dirección.

2. Comprobar usuario.
3. Pasar a HOME

[ ] - Navegar el listado de tiendas

1. Muestra lista de tiendas
2. Agregar espacio para el filtrado de tiendas.

   - filtrar por nombre
   - filtrar por rubro
   - filtrar por ubicación cercana. [minima prioridad]

[ ] - Navegar el catálogo de cada tienda.

1.  Muestra lista de productos. [ver https://github.com/android/sunflower]

2.  Filtrar por una o más condiciones:
    - nombre,
    - categoría [minima prioridad]
    - rango de precios. [minima prioridad]

[ ] - Realizar pedidos:

1. En una TIENDA -> Agregar boton para iniciar un PEDIDO
2. Mostrar SOLO productos disponibles en la TIENDA.
3. Cliquear y agregar elementos al canasto.
4. En el canasto -> boton para finalizar el mismo.
5. Mostrar los elementos que se encuentren en el stock al momento de cerrar el pedido y el precio total.

   ```
     Pedido #767765
     Estado: creado??

     Productos:
       [producto 1 (que no hay stock)][*]
       [producto 2                   ][x]
       [producto 4                   ][x]

     Total: $1000

     [Confirmar pedido][Cancela]
   ```

   Por cada elemento que no haya sido posible agregar al pedido, la aplicación debe mostrar las opciones [minima prioridad]:

   1. buscar producto alternativa (por ejemplo, un pote de dulce de leche de otra marca) o la otra opción
   2. de ignorar.

[ ] - Mostrar listado del pedidos realizados

1. Realizados (En curso)
2. Realizados (Historial)

[ ] - Recibir la notificación de pedido listo:

- Opción A: Recibir la notificación Firebase + cambia el color en el listado.
- Opción B: cambia el color en el listado.
