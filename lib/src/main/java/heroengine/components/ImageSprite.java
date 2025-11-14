package heroengine.components;

import heroengine.ecs.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 画像スプライトコンポーネント 画像ファイルを読み込んで表示
 */
public class ImageSprite implements Component {

    public BufferedImage image;
    public int width;
    public int height;
    public boolean visible;
    public int zOrder; // 描画順序（大きいほど手前）
    public float alpha; // 透明度 (0.0f-1.0f)

    /**
     * 画像ファイルパスから読み込み
     */
    public ImageSprite(String imagePath) throws IOException {
        this.image = ImageIO.read(new File(imagePath));
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.visible = true;
        this.zOrder = 0;
        this.alpha = 1.0f;
    }

    /**
     * InputStreamから読み込み（リソースファイル用）
     */
    public ImageSprite(InputStream imageStream) throws IOException {
        this.image = ImageIO.read(imageStream);
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.visible = true;
        this.zOrder = 0;
        this.alpha = 1.0f;
    }

    /**
     * 既存のBufferedImageから作成
     */
    public ImageSprite(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.visible = true;
        this.zOrder = 0;
        this.alpha = 1.0f;
    }

    /**
     * 画像を変更
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * 画像をファイルパスから読み込み
     */
    public void loadImage(String imagePath) throws IOException {
        this.image = ImageIO.read(new File(imagePath));
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * 表示サイズを設定（元画像のサイズは変更されない）
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 表示/非表示を設定
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * 描画順序を設定
     */
    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }

    /**
     * 透明度を設定 (0.0f=完全透明, 1.0f=不透明)
     */
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }

    /**
     * 元画像のサイズを取得
     */
    public int getOriginalWidth() {
        return image.getWidth();
    }

    /**
     * 元画像のサイズを取得
     */
    public int getOriginalHeight() {
        return image.getHeight();
    }
}
