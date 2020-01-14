# Aria Search API

## Run

```shell script
$ gradlew bootJar
$ docker-compose up -d
```

## Endpoint

### `GET /search`

#### Query parameters

| name | type | required | default |
|:---:|:---:|:---:|:---:|
| query | string | true | |
| provider | string | false | |
| offset | int | false | 0 |
| limit | int | false | 50 |

#### Returns

| name | type | nullable |
|:---:|:---:|:---:|
| hit | int | false |
| contains | int | false |
| entries | Entry | false |


### `POST /cache`

#### Parameters

| name | type | required | default |
|:---:|:---:|:---:|:---|
| entries | Array<Entry> | true | |


### `POST /cache/resolve`

#### Parameters

| name | type | required | default |
|:---:|:---:|:---:|:---:|
| uri | string | true | |


### `POST /playlist`

#### Parameters

| name | type | required | default |
|:---:|:---:|:---:|:---:|
| name | string | true | |


### `GET /playlist/{name}`

#### Path parameters

| name | type | required | default |
|:---:|:---:|:---:|:---:|
| name | string | true | |

#### Query parameters

| name | type | required | default |
|:---:|:---:|:---:|:---:|
| limit | int | false | 50 |
|offset | int | false | 0 |

#### Returns

| name | type | nullable |
|:---:|:---:|:---:|
| id | int | false |
| name | string | false |
| ownerId | int | false |
| groupId | int | true |
| entryURIs | List<string> | false |


### `POST /playlist/{name}`

#### Path parameters

| name | type | required | default |
|:---:|:---:|:---:|:---:|
| name | string | true | |

#### Parameters

| name | type | required | default |
|:---:|:---:|:---:|:---:|
| uris | List<string> | true | |

## Entities

### `Entry`

| name | type | nullable |
|:---:|:---:|:---:|
| uri | string | false |
| provider | string | false |
| title | string | false |
| thumbnail | string | true |
| liked | bool | true |
| meta | string | true |
