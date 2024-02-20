package com.s3project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class S3UsingJavaApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(S3UsingJavaApplication.class, args);
        
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter AWS Access Key: ");
        String accessKey = scanner.nextLine();

        System.out.print("Enter AWS Secret Key: ");
        String secretKey = scanner.nextLine();

        System.out.print("Enter AWS Region: ");
        String region = scanner.nextLine();

        // Creating s3Client
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();   

        try {
            //String bucketName = "saoodsdkbucket";

            System.out.print("Enter AWS S3 bucketName: ");
            String bucketName = scanner.nextLine();
            System.out.println("//********************************//");
            
            
            // Prompt user for object key to delete
            System.out.print("Enter the key of the object you want to delete: ");
            String objectKey = scanner.nextLine();
            // Check if the object exists
            if (s3.doesObjectExist(bucketName, objectKey)) {
                // Delete the object
                s3.deleteObject(bucketName, objectKey);
                System.out.println("Object with key " + objectKey + " deleted successfully from bucket " + bucketName);
            } else {
                System.out.println("Object with key " + objectKey + " does not exist in bucket " + bucketName);
            }
            
            // Check if the bucket exists
            if (s3.doesBucketExistV2(bucketName)) {
                System.out.println("Bucket " + bucketName + " exists. Deleting...");
                s3.deleteBucket(bucketName);
                System.out.println("Bucket " + bucketName + " deleted.");
            }

            // Create a new bucket
            //bucketName = "saoodsdkbucket";
            System.out.println("Creating bucket " + bucketName);
            s3.createBucket(bucketName);
            System.out.println("Bucket " + bucketName + " created successfully.");
            
            
         // Prompt user for file path
            System.out.print("Enter the path of the file to upload: ");
            String filePath = scanner.nextLine();
            File file = new File(filePath);

            // Check if the file exists
			/*
			 * if (!file.exists()) { System.out.println("File " + filePath +
			 * " does not exist."); return; }
			 */

            // Upload file to S3
            String key = file.getName();
            s3.putObject(new PutObjectRequest(bucketName, key, file));
            System.out.println("File uploaded successfully to bucket " + bucketName + " with key " + key);
            
            // List of all the buckets for that user
            List<Bucket> buckets = s3.listBuckets();
            
            // Iteration
            buckets.forEach(bucket -> {
                System.out.println("Bucket Name: " + bucket.getName() + 
                                   ", Bucket Owner: " + bucket.getOwner().getDisplayName() +
                                   ", Bucket Creation Date: " + bucket.getCreationDate());
            });
            
            
            System.out.println("Objects in bucket " + bucketName + ":");
            
            
            List<S3ObjectSummary> objects = s3.listObjects(bucketName).getObjectSummaries();
            if (objects.isEmpty()) {
                System.out.println("No objects found in the bucket.");
            } else {
                for (S3ObjectSummary object : objects) {
                    System.out.println(" - " + object.getKey() + " (size: " + object.getSize() + " bytes)");
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
