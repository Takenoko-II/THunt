# THunt

[FredHunt](https://github.com/fredthedoggy/fredhunt) をもとにした1.21.11対応のトラッカーコンパスプラグイン

## Usage

### /tracker
以下のコマンドの実行でトラッカーコンパスを実行者のインベントリに入れる
<br>(既に所持している場合はインベントリから削除する)
```mcfunction
# エイリアス: /manhunt, /compass, /thunt, /track, /hunt, /hunter
# すべて中身は同じコマンドです
/tracker
```

### left-click
左クリックで追跡対象セレクター(UI)を開く

### right-click
右クリックで追跡対象の位置情報を更新する
<br>ただし、以下の場合は特殊な挙動をする

#### 1. トラッカーコンパスに追跡対象が設定されていない場合
エラーメッセージが表示。コンパスの表示は変わらない。

#### 2. 追跡対象が既に存在しない場合
エラーメッセージが表示。コンパスの表示は変わらない。

#### 3. 追跡対象が別ディメンションにいる場合
追跡対象の最後の位置情報(例: ネザーポータルの位置)を示す
<br>(この機能は後述の `config.json` から無効化できます)

> [!WARNING]
> ポータルの位置情報はプレイヤーがサーバーから退出する度に破棄されます

### ドロップ
- Qキー等で投げることはできない
- `keep_inventory` がオフの状態で死亡しても、トラッカーコンパスはドロップしない
- クリエイティブモードインベントリからの削除は容易

### `config.json`
サーバー.jarを含むディレクトリから見て `plugins/THunt` の位置に `config.json` が入っている
<br>初期設定は以下:
```json
{
    "join_to_give_compass": true,
    "track_last_used_portal": true,
    "require_op_to_use_tracker": false
}

```

変更後に `/config reload` を実行するか、起動前に変更しておくことで設定を適用可能
<br> (`/config` の実行にはオペレーター相当の権限を必要とする)

#### `join_to_give_compass`
`true` のとき、プレイヤーの参加時にトラッカーコンパスを所持していなければ自動で付与

#### `track_last_used_portal`
`true` のとき、ポータル位置の追跡を行う (`false` で無効化され、別ディメンションの追跡対象は追跡できなくなる)

#### `require_op_to_use_tracker`
`true` のとき、コマンド `/tracker` の実行にオペレーター権限を要求する

## Note
- [FredHunt](https://github.com/fredthedoggy/fredhunt) のコードは一切再利用されていないため一部仕様が大きく異なる可能性があります

## Author / Contact
- [Takenoko-II](https://x.com/Takenoko_4096)
