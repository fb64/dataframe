[//]: # (title: flatten)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) without column groupings under selected columns.

```text
flatten  [ { columns } ]
```

Columns will keep their original names after flattening.
Potential column name clashes are resolved by adding minimal possible name prefix from ancestor columns.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN flatten-->
<tabs>
<tab title="Properties">

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.flatten { name }
```

</tab>
<tab title="Strings">

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.flatten("name")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.flatten.html" width="100%"/>
<!---END-->

To remove all column groupings in [`DataFrame`](DataFrame.md), invoke `flatten` without parameters:

<!---FUN flattenAll-->

```kotlin
df.flatten()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.flattenAll.html" width="100%"/>
<!---END-->
