[//]: # (title: ungroup)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces [`ColumnGroup`](DataColumn.md#columngroup) with its nested columns. 

```text
ungroup { columns }
```

**Reverse operation:** [`group`](group.md)

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN ungroup-->

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.ungroup { name }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.ungroup.html" width="100%"/>
<!---END-->
