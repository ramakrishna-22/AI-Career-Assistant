package com.careerassistant.config;

import com.careerassistant.entity.Job;
import com.careerassistant.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JobDataSeeder implements CommandLineRunner {

    private final JobRepository jobRepository;

    public JobDataSeeder(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void run(String... args) {
        if (jobRepository.count() == 0) {
            List<Job> sampleJobs = Arrays.asList(
                    new Job(
                            "Java Full Stack Developer",
                            "Tech Corp Technologies",
                            "Hyderabad",
                            "Design and development of enterprise web applications using Spring Boot, React, and MySQL. Experience in RESTful web services and cloud platforms.",
                            "Java, Spring Boot, REST API, React, HTML, CSS, JavaScript, MySQL, AWS",
                            "₹12,000,000 - ₹18,000,000 / yr",
                            "Full-time",
                            "2-4 years"
                    ),
                    new Job(
                            "Python Software Engineer",
                            "PyInnovations Labs",
                            "Bengaluru",
                            "Build scalable backend services, data pipelines, and REST APIs using Python, Django, and PostgreSQL.",
                            "Python, Django, Flask, SQL, REST API, Git, PostgreSQL, Docker",
                            "₹10,000,000 - ₹15,000,000 / yr",
                            "Full-time",
                            "1-3 years"
                    ),
                    new Job(
                            "Frontend Developer (React)",
                            "UI Solutions Inc",
                            "Remote / Mumbai",
                            "Craft high-performance, responsive SaaS user interfaces using React, Redux, HTML5, CSS3, and modern TypeScript.",
                            "JavaScript, React, HTML, CSS, Bootstrap, TypeScript, Git",
                            "₹8,000,000 - ₹14,000,000 / yr",
                            "Full-time",
                            "1-3 years"
                    ),
                    new Job(
                            "Senior Backend Engineer",
                            "CloudScale Systems",
                            "Bengaluru",
                            "High-availability microservices architecture in Java & Node.js with distributed database optimization.",
                            "Java, Spring Boot, Node.js, REST API, SQL, MySQL, PostgreSQL, Docker, Redis",
                            "₹18,000,000 - ₹28,000,000 / yr",
                            "Full-time",
                            "3-6 years"
                    ),
                    new Job(
                            "Software Development Engineer (SDE 1)",
                            "Global Logic Networks",
                            "Hyderabad",
                            "Solve complex algorithmic problems and build robust backend infrastructure components.",
                            "Java, Python, Data Structures, SQL, Git, REST API, MySQL",
                            "₹9,000,000 - ₹14,000,000 / yr",
                            "Full-time",
                            "0-2 years"
                    ),
                    new Job(
                            "AI / Machine Learning Engineer",
                            "NeuralTech AI",
                            "Bengaluru",
                            "Develop, evaluate, and deploy deep learning and NLP models into production cloud environments.",
                            "Python, Machine Learning, Deep Learning, TensorFlow, PyTorch, SQL, NLP, OpenCV",
                            "₹16,000,000 - ₹25,000,000 / yr",
                            "Full-time",
                            "2-5 years"
                    ),
                    new Job(
                            "Data Analyst",
                            "DataInsights Global",
                            "Pune",
                            "Transform structured and unstructured business data into actionable dashboard reports.",
                            "Python, SQL, MySQL, PostgreSQL, Git, Excel",
                            "₹7,000,000 - ₹11,000,000 / yr",
                            "Full-time",
                            "1-3 years"
                    ),
                    new Job(
                            "Cloud Infrastructure Engineer",
                            "Apex Cloud Solutions",
                            "Remote / Hyderabad",
                            "Design and manage AWS multi-region infrastructure using Terraform, Docker, and Kubernetes.",
                            "AWS, EC2, S3, IAM, Docker, Kubernetes, Linux, Git",
                            "₹15,000,000 - ₹22,000,000 / yr",
                            "Full-time",
                            "2-5 years"
                    ),
                    new Job(
                            "DevOps Automation Engineer",
                            "Streamline DevOps",
                            "Chennai",
                            "Automate CI/CD pipelines, container orchestration, monitoring, and zero-downtime deployments.",
                            "Docker, Kubernetes, Jenkins, CI/CD, Git, AWS, Linux, Python",
                            "₹14,000,000 - ₹20,000,000 / yr",
                            "Full-time",
                            "2-4 years"
                    ),
                    new Job(
                            "Junior Full Stack Developer",
                            "Startup Launchpad",
                            "Remote",
                            "Great entry-level role for junior developers passionate about Java, Spring Boot, React, and databases.",
                            "Java, JavaScript, React, HTML, CSS, SQL, Git, Spring Boot",
                            "₹6,000,000 - ₹9,000,000 / yr",
                            "Full-time",
                            "0-1 years"
                    )
            );
            jobRepository.saveAll(sampleJobs);
            System.out.println("=== SEEDED " + sampleJobs.size() + " SAMPLE JOBS FOR WEEK 4 ===");
        }
    }
}
