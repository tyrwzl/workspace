//
//  main.cpp
//  OpenCV Test
//
//  Created by Takahiro YAMADA on 2015/09/30.
//  Copyright © 2015年 Takahiro YAMADA. All rights reserved.
//

/*
#include <iostream>

int main(int argc, const char * argv[]) {
    // insert code here...
    std::cout << "Hello, World!\n";
    return 0;
}
*/
#include <cxcore.h>
#include <cv.h>
#include <highgui.h>

int
main (int argc, char **argv)
{
    IplImage *src_img = 0, *dst_img;
    IplImage *src_img_gray = 0;
    IplImage *tmp_img;
    CvMemStorage *storage = cvCreateMemStorage (0);
    CvSeq *contours = 0;
    CvBox2D ellipse;
    CvTreeNodeIterator it;
    CvPoint2D32f pt[4];
    
    // (1)画像を読み込む
    if (argc >= 2)
        src_img = cvLoadImage (argv[1], CV_LOAD_IMAGE_COLOR);
    if (src_img == 0)
        return -1;
    
    src_img_gray = cvCreateImage (cvGetSize (src_img), IPL_DEPTH_8U, 1);
    cvCvtColor (src_img, src_img_gray, CV_BGR2GRAY);
    tmp_img = cvCreateImage (cvGetSize (src_img), IPL_DEPTH_8U, 1);
    dst_img = cvCloneImage (src_img);
    
    
    // (2)二値化と輪郭の検出
    cvThreshold (src_img_gray, tmp_img, 95, 255, CV_THRESH_BINARY);
    cvFindContours (tmp_img, storage, &contours, sizeof (CvContour),
                    CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, cvPoint (0, 0));
    
    // (3)ツリーノードイテレータの初期化
    cvInitTreeNodeIterator (&it, contours, 3);
    while ((contours = (CvSeq *) cvNextTreeNode (&it)) != NULL) {
        if (contours->total > 6) {
            // (4)楕円のフィッティング
            ellipse = cvFitEllipse2 (contours);
            ellipse.angle = 90.0 - ellipse.angle;
            // (5)輪郭，楕円，包含矩形の描画
            cvDrawContours (dst_img, contours, CV_RGB (255, 0, 0), CV_RGB (255, 0, 0), 0, 1, CV_AA, cvPoint (0, 0));
            cvEllipseBox (dst_img, ellipse, CV_RGB (0, 0, 255), 2);
            cvBoxPoints (ellipse, pt);
            cvLine (dst_img, cvPointFrom32f (pt[0]), cvPointFrom32f (pt[1]), CV_RGB (0, 255, 0));
            cvLine (dst_img, cvPointFrom32f (pt[1]), cvPointFrom32f (pt[2]), CV_RGB (0, 255, 0));
            cvLine (dst_img, cvPointFrom32f (pt[2]), cvPointFrom32f (pt[3]), CV_RGB (0, 255, 0));
            cvLine (dst_img, cvPointFrom32f (pt[3]), cvPointFrom32f (pt[0]), CV_RGB (0, 255, 0));
        }
    }
    
    // (6)画像の表示
    cvNamedWindow ("Fitting", CV_WINDOW_AUTOSIZE);
    cvShowImage ("Fitting", dst_img);
    cvWaitKey (0);
    
    cvDestroyWindow ("Fitting");
    cvReleaseImage (&src_img);
    cvReleaseImage (&dst_img);
    cvReleaseImage (&src_img_gray);
    cvReleaseImage (&tmp_img);
    cvReleaseMemStorage (&storage);
    
    return 0;
}